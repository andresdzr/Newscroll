package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun AddFriendScreen(userViewModel: UserViewModel) {

    val userId = userViewModel.getCurrentUserId()

    var userIdInput by remember { mutableStateOf("") }

    //  solicitudes (placeholder por ahora)
    var requests by remember { mutableStateOf<List<String>>(emptyList()) }

    //  cargar solicitudes al entrar
    LaunchedEffect(Unit) {
        userViewModel.getFriendRequests { result ->
            requests = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val clipboard = LocalClipboardManager.current
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(16.dp))

        Text("Tu userId:")

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(userId ?: "")

            TextButton(
                onClick = {
                    userId?.let { id: String ->
                        clipboard.setText(AnnotatedString(id))
                        Toast.makeText(context, "ID copiado", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Copiar")
            }
        }

        Text(
            text = "Añadir amigo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 INPUT
        OutlinedTextField(
            value = userIdInput,
            onValueChange = { userIdInput = it },
            label = { Text("ID del usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // BOTÓN ENVIAR
        Button(
            onClick = {
                if (userIdInput.isNotBlank()) {
                    userViewModel.sendFriendRequest(userIdInput)
                    userIdInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar solicitud")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Solicitudes de amistad",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LISTA SOLICITUDES
        if (requests.isEmpty()) {

            Text("No hay solicitudes pendientes")

        } else {

            LazyColumn {
                items(requests) { requestUserId ->

                    FriendRequestItem(
                        userId = requestUserId,
                        onAccept = {
                            userViewModel.acceptFriendRequest(requestUserId)

                            // RECARGAR
                            userViewModel.getFriendRequests {
                                requests = it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    userId: String,
    onAccept: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(userId)

        Button(onClick = onAccept) {
            Text("Aceptar")
        }
    }
}