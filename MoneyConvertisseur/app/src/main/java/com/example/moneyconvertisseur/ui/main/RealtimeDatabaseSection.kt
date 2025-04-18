// RealtimeDatabaseSection.kt
package com.example.moneyconvertisseur.ui.main

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

@Composable
fun RealtimeDatabaseSection(
    userMessageReference: DatabaseReference
) {
    var amountsList by remember { mutableStateOf<List<Double>>(emptyList()) }
    var dbError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(userMessageReference) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val amounts = mutableListOf<Double>()
                snapshot.children.forEach { child ->
                    child.getValue(String::class.java)?.toDoubleOrNull()?.let {
                        amounts.add(it)
                    }
                }
                amountsList = amounts
                dbError = null
                Log.d("RTDBSection", "Data received: ${amounts.size} amounts")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RTDBSection", "Database read failed: ${error.message}", error.toException())
                amountsList = emptyList()
                dbError = "Erreur lecture BDD: ${error.message}"
            }
        }

        Log.d("RTDBSection", "Adding ValueEventListener for path: ${userMessageReference.toString()}")
        userMessageReference.addValueEventListener(listener)

        onDispose {
            Log.d("RTDBSection", "Removing ValueEventListener for path: ${userMessageReference.toString()}")
            userMessageReference.removeEventListener(listener)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (dbError != null) {
            Text(dbError!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text("Somme des dépenses (€):")
        Text(
            text = if (amountsList.isNotEmpty()) {
                "%.2f".format(amountsList.sum())
            } else {
                "(Aucune donnée)"
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            text = "${amountsList.size} dépenses enregistrées",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}