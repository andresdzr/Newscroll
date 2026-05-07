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
import androidx.compose.ui.res.stringResource

@Composable
fun AddFriendScreen(userViewModel: UserViewModel) {

    val userId = userViewModel.getCurrentUserId()

    // Input state and friend requests list
    var userIdInput by remember { mutableStateOf("") }
    var requests by remember { mutableStateOf<List<FriendRequest>>(emptyList()) }

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    // Load friend requests on screen start
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

        // Screen title
        Text(
            text = stringResource(R.string.add_friends),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card displaying current user ID
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = stringResource(R.string.your_id),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Show user ID
                    Text(
                        text = userId,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Copy ID to clipboard
                    IconButton(
                        onClick = {
                            if (userId.isNotEmpty()) {
                                clipboard.setText(AnnotatedString(userId))
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.id_copied),
                                    Toast.LENGTH_SHORT
                                ).show()
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
                            contentDescription = "Copy ID",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Input section title
        Text(
            text = stringResource(R.string.add_friend),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Input field for friend ID
        OutlinedTextField(
            value = userIdInput,
            onValueChange = { userIdInput = it },
            placeholder = {
                Text(stringResource(R.string.enter_id))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Send friend request button
        Button(
            onClick = {
                if (userIdInput.isNotBlank()) {
                    userViewModel.sendFriendRequest(userIdInput)
                    userIdInput = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(stringResource(R.string.send_request))
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Requests section title
        Text(
            text = stringResource(R.string.requests),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (requests.isEmpty()) {

            // Empty state
            Text(
                text = stringResource(R.string.no_pending_requests),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        } else {

            // List of incoming friend requests
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

                            // Refresh requests after accepting
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

        // Request sender ID
        Text(userId)

        // Accept request action
        Button(onClick = onAccept) {
            Text(stringResource(R.string.accept))
        }
    }
}