// HomeScreen.kt
package com.example.moneyconvertisseur.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// Constante URL de Firebase Realtime Database
private const val URL_RTDB = "https://moneyconvertisseur-default-rtdb.europe-west1.firebasedatabase.app"

@Composable
fun HomeScreen(user: FirebaseUser, onLogout: () -> Unit) {
    val userMessageRef: DatabaseReference = remember(user.uid) {
        FirebaseDatabase.getInstance(URL_RTDB)
            .getReference("userMessages")
            .child(user.uid)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Bienvenue",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section Dépenses
        RealtimeDatabaseSection(userMessageReference = userMessageRef)

        Spacer(modifier = Modifier.height(32.dp))

        // Section Convertisseur
        CurrencyConverterSection(userMessageRef)

        Spacer(modifier = Modifier.weight(1f))

        // Bouton Déconnexion
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se déconnecter")
        }
    }
}

@Composable
private fun CurrencyConverterSection(userMessageRef: DatabaseReference) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Convertisseur de devises",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            var amount by remember { mutableStateOf("") }
            var fromCurrency by remember { mutableStateOf("EUR") }
            var toCurrency by remember { mutableStateOf("USD") }
            var result by remember { mutableStateOf("") }

            val currencies = listOf("EUR", "USD", "GBP", "MYR", "CAD")

            val exchangeRates = mapOf(
                "EUR-USD" to 1.08, "USD-EUR" to 0.93,
                "EUR-GBP" to 0.86, "GBP-EUR" to 1.16,
                "EUR-MYR" to 5.12, "MYR-EUR" to 0.20,
                "EUR-CAD" to 1.47, "CAD-EUR" to 0.68,
                "USD-GBP" to 0.79, "GBP-USD" to 1.26,
                "USD-MYR" to 4.74, "MYR-USD" to 0.21,
                "USD-CAD" to 1.36, "CAD-USD" to 0.74,
                "GBP-MYR" to 6.00, "MYR-GBP" to 0.17,
                "GBP-CAD" to 1.72, "CAD-GBP" to 0.58,
                "MYR-CAD" to 0.29, "CAD-MYR" to 3.45
            )

            // Champ de saisie du montant
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it.filter { char -> char.isDigit() || char == '.' }
                },
                label = { Text("Montant") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sélection des devises
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CurrencyDropdown(
                    selectedCurrency = fromCurrency,
                    onCurrencySelected = { fromCurrency = it },
                    label = "De",
                    currencies = currencies
                )

                CurrencyDropdown(
                    selectedCurrency = toCurrency,
                    onCurrencySelected = { toCurrency = it },
                    label = "À",
                    currencies = currencies
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton Convertir
            Button(
                onClick = {
                    if (amount.isNotBlank()) {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        val rateKey = "$fromCurrency-$toCurrency"
                        val rate = exchangeRates[rateKey] ?: 1.0
                        val convertedAmount = amountValue * rate
                        result = "%.2f %s = %.2f %s".format(
                            amountValue,
                            fromCurrency,
                            convertedAmount,
                            toCurrency
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Convertir", style = MaterialTheme.typography.labelLarge)
            }

            // Affichage du résultat
            if (result.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    result,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                val rateKey = "$fromCurrency-$toCurrency"
                val rate = exchangeRates[rateKey] ?: 1.0
                Text(
                    "Taux: 1 $fromCurrency = ${String.format("%.4f", rate)} $toCurrency",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bouton Sauvegarder (modifié pour sauvegarder en euros)
            Button(
                onClick = {
                    if (amount.isNotBlank()) {
                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        // Convertir le montant en euros avant sauvegarde
                        val amountInEuro = convertToEuro(amountValue, fromCurrency, exchangeRates)
                        val newAmountRef = userMessageRef.push()
                        // Sauvegarder le montant en euros avec 2 décimales
                        newAmountRef.setValue(String.format("%.2f", amountInEuro))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text("Sauvegarder le montant en euros")
            }
        }
    }
}

private fun convertToEuro(amount: Double, fromCurrency: String, exchangeRates: Map<String, Double>): Double {
    if (fromCurrency == "EUR") return amount
    val rateKey = "$fromCurrency-EUR"
    val rate = exchangeRates[rateKey] ?: 1.0
    return amount * rate
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    label: String,
    currencies: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.width(120.dp)) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
            ) {
                Text(selectedCurrency, style = MaterialTheme.typography.bodyLarge)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(120.dp)
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = {
                            Text(currency, style = MaterialTheme.typography.bodyLarge)
                        },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
