package com.pdm.proiect_android_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.pdm.proiect_android_kotlin.service.FirebaseService
import kotlinx.coroutines.*

class LoginActivity : ComponentActivity() {
    private val firebaseService = FirebaseService(this)

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
    }

    fun signIn(view: View) {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            val success = firebaseService.authenticateUser(email, password, true)
            if (success) {
                Toast.makeText(this@LoginActivity, "Sign in successful", Toast.LENGTH_SHORT).show()
                navigateToMain(email)
            } else {
                Toast.makeText(this@LoginActivity, "Sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun signUp(view: View) {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        CoroutineScope(Dispatchers.Main).launch {
            val success = firebaseService.authenticateUser(email, password, false)
            if (success) {
                Toast.makeText(this@LoginActivity, "Sign up successful", Toast.LENGTH_SHORT).show()
                navigateToMain(email)
            } else {
                Toast.makeText(this@LoginActivity, "Sign up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain(email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
        finish()
    }
}
