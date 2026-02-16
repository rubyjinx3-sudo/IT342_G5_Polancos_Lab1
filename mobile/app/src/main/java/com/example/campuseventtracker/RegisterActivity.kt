package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : Activity() {

    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvSuccess = findViewById<TextView>(R.id.tvSuccess)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validation
            when {
                username.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    tvError.text = "Please fill in all fields"
                    tvError.visibility = View.VISIBLE
                    tvSuccess.visibility = View.GONE
                }
                username.length < 3 -> {
                    tvError.text = "Username must be at least 3 characters"
                    tvError.visibility = View.VISIBLE
                    tvSuccess.visibility = View.GONE
                }
                password.length < 6 -> {
                    tvError.text = "Password must be at least 6 characters"
                    tvError.visibility = View.VISIBLE
                    tvSuccess.visibility = View.GONE
                }
                !email.contains("@") -> {
                    tvError.text = "Please enter a valid email"
                    tvError.visibility = View.VISIBLE
                    tvSuccess.visibility = View.GONE
                }
                else -> {
                    tvError.visibility = View.GONE
                    btnRegister.isEnabled = false
                    btnRegister.text = "Registering..."
                    register(username, email, password, tvError, tvSuccess, btnRegister)
                }
            }
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun register(
        username: String,
        email: String,
        password: String,
        tvError: TextView,
        tvSuccess: TextView,
        btnRegister: Button
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL/auth/register")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doOutput = true

                val jsonBody = """{"username":"$username","email":"$email","password":"$password"}"""
                connection.outputStream.write(jsonBody.toByteArray())

                val responseCode = connection.responseCode

                withContext(Dispatchers.Main) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"

                    if (responseCode == 200) {
                        tvSuccess.text = "Registration successful! Redirecting to login..."
                        tvSuccess.visibility = View.VISIBLE
                        tvError.visibility = View.GONE

                        // Wait 2 seconds then go to login
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        tvError.text = "Registration failed. Username or email may already exist."
                        tvError.visibility = View.VISIBLE
                        tvSuccess.visibility = View.GONE
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"
                    tvError.text = "Connection failed. Is the server running?"
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}