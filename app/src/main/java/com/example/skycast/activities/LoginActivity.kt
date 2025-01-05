package com.example.skycast.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreen(
                    onLogin = { email, password -> loginUser(email, password) },
                    onSignUp = { startActivity(Intent(this, SignUpActivity::class.java)) },
                    onForgotPassword = { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
                )
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Email not verified. Please verify your email to log in.",
                            Toast.LENGTH_LONG
                        ).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF512DA8)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                onLogin(email, password)
                            } else {
                                showToast = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Log In",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (showToast) {
                        ToastMessage("Please fill in all fields")
                    }

                    TextButton(onClick = onForgotPassword) {
                        Text(
                            text = "Forgot Password?",
                            style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Medium)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onSignUp) {
                        Text(
                            text = "Don't have an account? Sign Up",
                            style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToastMessage(message: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}