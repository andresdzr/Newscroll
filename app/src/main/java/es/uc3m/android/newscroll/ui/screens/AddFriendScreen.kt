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
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import es.uc3m.android.newscroll.R
@Composable
fun AddFriendScreen(userViewModel: UserViewModel) {

    val userId = userViewModel.getCurrentUserId()

    var userIdInput by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf<List<FriendRequest>>(emptyList()) }

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.getFriendRequests {
            requests = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

        // 🔹 HEADER
        Text(
            text = "Añadir amigos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 CARD: USER ID
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Tu ID",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = userId,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    IconButton(
                        onClick = {
                            if (userId.isNotEmpty()) {
                                clipboard.setText(AnnotatedString(userId))
                                Toast.makeText(context, "ID copiado", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
                            contentDescription = "Copiar ID",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔹 INPUT SECTION
        Text(
            text = "Añadir amigo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = userIdInput,
            onValueChange = { userIdInput = it },
            placeholder = { Text("Introduce un ID") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (userIdInput.isNotBlank()) {
                    userViewModel.sendFriendRequest(userIdInput)
                    userIdInput = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp), // botón más sólido
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Enviar solicitud")
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 🔹 REQUESTS SECTION
        Text(
            text = "Solicitudes",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (requests.isEmpty()) {

            Text(
                text = "No hay solicitudes pendientes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests) { request ->

                    FriendRequestItem(
                        userId = request.fromUserId,
                        onAccept = {
                            userViewModel.acceptFriendRequest(
                                request.id,
                                request.fromUserId
                            )

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