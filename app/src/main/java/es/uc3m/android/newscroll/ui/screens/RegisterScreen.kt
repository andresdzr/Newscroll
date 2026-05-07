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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import es.uc3m.android.newscroll.R
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

    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // Screen title
        Text(
            text = stringResource(R.string.create_account_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = null // clear error on input change
            },
            label = {
                Text(stringResource(R.string.email))
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = null
            },
            label = {
                Text(stringResource(R.string.password))
            },
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

        // Repeat password input field
        OutlinedTextField(
            value = repeatPassword,
            onValueChange = {
                repeatPassword = it
                error = null
            },
            label = {
                Text(stringResource(R.string.repeat_password))
            },
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

        // Password requirements hint
        Text(
            text = stringResource(R.string.password_min_chars),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Register button with validation
        Button(
            onClick = {
                when {
                    // Password mismatch validation
                    password != repeatPassword -> {
                        error = context.getString(R.string.passwords_do_not_match)
                        password = ""
                        repeatPassword = ""
                    }

                    // Minimum length validation
                    password.length < 6 -> {
                        error = "La contraseña debe tener al menos 6 caracteres"
                        password = ""
                        repeatPassword = ""
                    }

                    // Register user
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
            Text(stringResource(R.string.create_account))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate back to login
        TextButton(onClick = onBack) {
            Text(stringResource(R.string.back))
        }
    }
}