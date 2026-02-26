package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : Activity() {

    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val spinnerRole   = findViewById<Spinner>(R.id.spinnerRole)
        val btnRegister   = findViewById<Button>(R.id.btnRegister)
        val tvError       = findViewById<TextView>(R.id.tvError)
        val tvSuccess     = findViewById<TextView>(R.id.tvSuccess)
        val tvLoginLink   = findViewById<TextView>(R.id.tvLoginLink)

        // Role spinner options — must match backend enum: STUDENT, ORGANIZER
        val roles = listOf("STUDENT", "ORGANIZER")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRole.adapter = adapter

        btnRegister.setOnClickListener {
            val fullName        = etFullName.text.toString().trim()
            val email           = etEmail.text.toString().trim()
            val password        = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val role            = spinnerRole.selectedItem.toString()

            // Validation
            when {
                fullName.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    showError(tvError, tvSuccess, "Please fill in all required fields")
                    return@setOnClickListener
                }
                fullName.length < 2 -> {
                    showError(tvError, tvSuccess, "Full name must be at least 2 characters")
                    return@setOnClickListener
                }
                !email.contains("@") -> {
                    showError(tvError, tvSuccess, "Please enter a valid email")
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    showError(tvError, tvSuccess, "Password must be at least 6 characters")
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    showError(tvError, tvSuccess, "Passwords do not match")
                    return@setOnClickListener
                }
            }

            tvError.visibility = View.GONE
            btnRegister.isEnabled = false
            btnRegister.text = "Creating account..."

            register(fullName, email, password, role, tvError, tvSuccess, btnRegister)
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun register(
        fullName: String,
        email: String,
        password: String,
        role: String,
        tvError: TextView,
        tvSuccess: TextView,
        btnRegister: Button
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("$BASE_URL/auth/register")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout    = 8000
                conn.doOutput = true

                // Backend RegisterRequest expects: { fullName, email, password, role }
                val body = JSONObject()
                body.put("fullName", fullName)
                body.put("email",    email)
                body.put("password", password)
                body.put("role",     role)   // "STUDENT" or "ORGANIZER"
                conn.outputStream.write(body.toString().toByteArray())

                val code         = conn.responseCode
                val responseBody = if (code == 200)
                    conn.inputStream.bufferedReader().readText()
                else
                    conn.errorStream?.bufferedReader()?.readText() ?: ""

                conn.disconnect()

                withContext(Dispatchers.Main) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Create Account"

                    if (code == 200) {
                        tvSuccess.text = "Account created! Redirecting to login..."
                        tvSuccess.visibility = View.VISIBLE
                        tvError.visibility = View.GONE

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1500)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        // Extract error message from plain string or JSON
                        val msg = try {
                            JSONObject(responseBody).optString("message", responseBody)
                        } catch (e: Exception) { responseBody }
                        showError(tvError, tvSuccess, msg.ifEmpty { "Registration failed. Email may already exist." })
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Create Account"
                    showError(tvError, tvSuccess, "Connection failed. Is the server running?")
                }
            }
        }
    }

    private fun showError(tvError: TextView, tvSuccess: TextView, msg: String) {
        tvError.text = msg
        tvError.visibility = View.VISIBLE
        tvSuccess.visibility = View.GONE
    }
}