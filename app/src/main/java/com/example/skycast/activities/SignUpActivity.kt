package com.example.skycast.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import com.example.skycast.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SignUpScreen(
                    onSignUp = { email, password, confirmPassword ->
                        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                        } else if (password.length < 6) {
                            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        } else {
                            registerUser(email, password)
                        }
                    },
                    onLogin = { startActivity(Intent(this, LoginActivity::class.java)) }
                )
            }
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { sendVerificationEmail(it) }
                    Toast.makeText(this, "Registration Successful. A verification email has been sent.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendVerificationEmail(user: FirebaseUser) {
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String) -> Unit,
    onLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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
                painter = painterResource(id = R.drawable.ic_signup_illustration),
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
                        text = "Sign Up",
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

                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onSignUp(email, password, confirmPassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Sign Up",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onLogin) {
                        Text(
                            text = "Already have an account? Log in",
                            style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}