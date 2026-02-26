package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : Activity() {

    // ⚠️ Use your computer's local IP (not localhost) for physical device
    // For emulator use: 10.0.2.2
    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val tvError    = findViewById<TextView>(R.id.tvError)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // Already logged in → skip to dashboard
        val sharedPref = getSharedPreferences("ced_user", MODE_PRIVATE)
        if (sharedPref.getString("email", null) != null) {
            goToDashboard()
            return
        }

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError(tvError, "Please fill in all fields")
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            btnLogin.isEnabled = false
            btnLogin.text = "Signing in..."

            login(email, password, tvError, btnLogin, sharedPref)
        }

        tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(
        email: String,
        password: String,
        tvError: TextView,
        btnLogin: Button,
        sharedPref: SharedPreferences
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL/auth/login")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout    = 8000
                conn.doOutput = true

                // Backend LoginRequest expects: { email, password }
                val body = JSONObject()
                body.put("email", email)
                body.put("password", password)
                conn.outputStream.write(body.toString().toByteArray())

                val code         = conn.responseCode
                val responseBody = if (code == 200)
                    conn.inputStream.bufferedReader().readText()
                else
                    conn.errorStream?.bufferedReader()?.readText() ?: ""

                conn.disconnect()

                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"

                    if (code == 200) {
                        // Parse AuthResponse: { message, fullName, email, role, userId }
                        val json = JSONObject(responseBody)
                        sharedPref.edit()
                            .putString("email",    json.optString("email"))
                            .putString("fullName", json.optString("fullName"))
                            .putString("role",     json.optString("role"))
                            .putLong("userId",     json.optLong("userId"))
                            .apply()
                        goToDashboard()
                    } else {
                        // Backend returns plain string on error
                        val msg = try {
                            JSONObject(responseBody).optString("message", responseBody)
                        } catch (e: Exception) { responseBody }
                        showError(tvError, msg.ifEmpty { "Invalid email or password" })
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Login"
                    showError(tvError, "Connection failed. Is the server running?")
                }
            }
        }
    }

    private fun showError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = View.VISIBLE
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}