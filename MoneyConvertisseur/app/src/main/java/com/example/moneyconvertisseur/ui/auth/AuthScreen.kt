package com.example.moneyconvertisseur.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(auth: FirebaseAuth, onAuthComplete: (FirebaseUser) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) } // Nouveau message de succès
    var isLoading by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) } // Nouveau state pour le dialogue

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLoginMode) "Connexion" else "Inscription",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Champ Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Champ Mot de passe (seulement en mode connexion/inscription)
            if (!showForgotPasswordDialog) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mot de passe") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {

                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton Mot de passe oublié (seulement en mode connexion)
            if (isLoginMode && !showForgotPasswordDialog) {
                TextButton(
                    onClick = { showForgotPasswordDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Mot de passe oublié ?",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Bouton pour changer de mode (Connexion/Inscription)
            if (!showForgotPasswordDialog) {
                TextButton(
                    onClick = {
                        isLoginMode = !isLoginMode
                        errorMessage = null
                        successMessage = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = if (isLoginMode) "Pas de compte ? S'inscrire" else "Déjà un compte ? Se connecter",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Affichage des messages
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage!!,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // Bouton principal (change selon le contexte)
            Button(
                onClick = {
                    if (showForgotPasswordDialog) {
                        // Logique pour mot de passe oublié
                        if (email.isBlank()) {
                            errorMessage = "Veuillez entrer votre email"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        successMessage = null

                        coroutineScope.launch {
                            try {
                                auth.sendPasswordResetEmail(email).await()
                                successMessage = "Email de réinitialisation envoyé à $email"
                                showForgotPasswordDialog = false
                            } catch (e: Exception) {
                                errorMessage = "Erreur: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        // Logique normale de connexion/inscription
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Veuillez remplir tous les champs"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        successMessage = null

                        coroutineScope.launch {
                            try {
                                val result = if (isLoginMode) {
                                    auth.signInWithEmailAndPassword(email, password).await()
                                } else {
                                    auth.createUserWithEmailAndPassword(email, password).await()
                                }
                                result.user?.let { onAuthComplete(it) }
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Email ou mot de passe incorrect"
                            } catch (e: FirebaseAuthUserCollisionException) {
                                errorMessage = "Cet email est déjà utilisé"
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                errorMessage = "Mot de passe trop faible (6 caractères minimum)"
                            } catch (e: Exception) {
                                errorMessage = "Erreur: ${e.localizedMessage}"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        when {
                            showForgotPasswordDialog -> "Envoyer le lien de réinitialisation"
                            isLoginMode -> "Se connecter"
                            else -> "S'inscrire"
                        }
                    )
                }
            }

            // Bouton Retour dans le mode mot de passe oublié
            if (showForgotPasswordDialog) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        showForgotPasswordDialog = false
                        errorMessage = null
                        successMessage = null
                    }
                ) {
                    Text("Retour à la connexion", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}