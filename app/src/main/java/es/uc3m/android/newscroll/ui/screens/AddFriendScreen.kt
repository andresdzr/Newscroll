package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel
import es.uc3m.android.newscroll.viewmodel.FriendRequest
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun AddFriendScreen(userViewModel: UserViewModel) {

    val userId = userViewModel.getCurrentUserId()

    var userIdInput by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf<List<FriendRequest>>(emptyList()) }

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    // cargar solicitudes
    LaunchedEffect(Unit) {
        userViewModel.getFriendRequests {
            requests = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // TU USER ID
        Text("Tu userId:")

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(userId)

            TextButton(
                onClick = {
                    if (userId.isNotEmpty()) {
                        clipboard.setText(AnnotatedString(userId))
                        Toast.makeText(context, "ID copiado", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Copiar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Añadir amigo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INPUT
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
                items(requests) { request ->

                    FriendRequestItem(
                        userId = request.fromUserId,
                        onAccept = {
                            userViewModel.acceptFriendRequest(
                                request.id,
                                request.fromUserId
                            )

                            // recargar después de aceptar
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