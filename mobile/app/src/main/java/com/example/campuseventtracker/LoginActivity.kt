package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : Activity() {

    // ⚠️ Use your computer's IP for emulator
    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // Check if already logged in
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        if (sharedPref.getString("username", null) != null) {
            goToDashboard()
            return
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                tvError.text = "Please fill in all fields"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            btnLogin.isEnabled = false
            btnLogin.text = "Logging in..."

            login(username, password, tvError, btnLogin, sharedPref)
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(
        username: String,
        password: String,
        tvError: TextView,
        btnLogin: Button,
        sharedPref: SharedPreferences
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL/auth/login")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doOutput = true

                val jsonBody = """{"username":"$username","password":"$password"}"""
                connection.outputStream.write(jsonBody.toByteArray())

                val responseCode = connection.responseCode

                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"

                    if (responseCode == 200) {
                        // Save to SharedPreferences (like localStorage)
                        sharedPref.edit()
                            .putString("username", username)
                            .putString("password", password)
                            .apply()
                        goToDashboard()
                    } else {
                        tvError.text = "Invalid username or password"
                        tvError.visibility = View.VISIBLE
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                    tvError.text = "Connection failed. Is the server running?"
                    tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}