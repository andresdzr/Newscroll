package es.uc3m.android.newscroll.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import es.uc3m.android.newscroll.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    // Error LOCAL
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Crear cuenta en Newscroll",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = null
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible)
                    Icons.Default.VisibilityOff
                else
                    Icons.Default.Visibility

                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // REPEAT PASSWORD
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = {
                repeatPassword = it
                error = null
            },
            label = { Text("Repetir contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (repeatPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (repeatPasswordVisible)
                    Icons.Default.VisibilityOff
                else
                    Icons.Default.Visibility

                IconButton(onClick = {
                    repeatPasswordVisible = !repeatPasswordVisible
                }) {
                    Icon(icon, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "La contraseña debe tener al menos 6 caracteres",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ERROR
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                when {
                    password != repeatPassword -> {
                        error = "Las contraseñas no coinciden"
                        password = ""
                        repeatPassword = ""
                    }
                    password.length < 6 -> {
                        error = "La contraseña debe tener al menos 6 caracteres"
                        password = ""
                        repeatPassword = ""
                    }
                    else -> {
                        userViewModel.register(email, password) { errorMsg ->
                            error = errorMsg
                            password = ""
                            repeatPassword = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}