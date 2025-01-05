package com.example.skycast.activities

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordActivity : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ForgotPasswordScreen(
                    onResetPassword = { email -> resetPassword(email) },
                    onBackToLogin = { finish() }
                )
            }
        }
    }

    private fun resetPassword(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(this) { resetTask ->
                            if (resetTask.isSuccessful) {
                                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this, "Error: ${resetTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Invalid email. Please check and try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun ForgotPasswordScreen(
    onResetPassword: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }

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
                painter = painterResource(id = R.drawable.forgot_password_illustration),
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
                        text = "Reset Your Password",
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

                    Button(
                        onClick = { onResetPassword(email) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF512DA8)),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Send Reset Email",
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = "Back to Login",
                            style = TextStyle(color = Color(0xFF512DA8), fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }
}