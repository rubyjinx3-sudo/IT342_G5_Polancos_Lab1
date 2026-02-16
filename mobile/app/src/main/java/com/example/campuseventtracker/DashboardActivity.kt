package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class DashboardActivity : Activity() {

    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Get username from SharedPreferences
        val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
        val username = sharedPref.getString("username", "User")

        // If not logged in, go back to login
        if (username == null) {
            goToLogin()
            return
        }

        // Set welcome message
        tvWelcome.text = "Welcome, $username!"

        // Logout button
        btnLogout.setOnClickListener {
            btnLogout.isEnabled = false
            btnLogout.text = "Logging out..."
            logout(sharedPref)
        }
    }

    private fun logout(sharedPref: android.content.SharedPreferences) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call backend logout endpoint
                val url = URL("$BASE_URL/auth/logout")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.connect()
                connection.responseCode // trigger the request
                connection.disconnect()
            } catch (e: Exception) {
                // Even if backend fails, still logout locally
            } finally {
                withContext(Dispatchers.Main) {
                    // Clear saved credentials
                    sharedPref.edit().clear().apply()
                    goToLogin()
                }
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}