package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        val sharedPref = getSharedPreferences(AppConfig.USER_PREFS, MODE_PRIVATE)
        if (sharedPref.getString("email", null) != null) {
            goToDashboard()
            return
        }

        configurePasswordToggle(etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError(tvError, getString(R.string.error_fill_all_fields))
                return@setOnClickListener
            }

            tvError.visibility = View.GONE
            btnLogin.isEnabled = false
            btnLogin.text = getString(R.string.button_signing_in)

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
                val url = URL("${AppConfig.BASE_URL}/auth/login")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.doOutput = true

                val body = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                conn.outputStream.write(body.toString().toByteArray())

                val code = conn.responseCode
                val responseBody = if (code == 200) {
                    conn.inputStream.bufferedReader().readText()
                } else {
                    conn.errorStream?.bufferedReader()?.readText() ?: ""
                }

                conn.disconnect()

                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.button_login)

                    if (code == 200) {
                        val json = JSONObject(responseBody)
                        sharedPref.edit()
                            .putString("email", json.optString("email"))
                            .putString("fullName", json.optString("fullName"))
                            .putString("role", json.optString("role"))
                            .putLong("userId", json.optLong("userId"))
                            .apply()
                        goToDashboard()
                    } else {
                        val msg = try {
                            JSONObject(responseBody).optString("message", responseBody)
                        } catch (_: Exception) {
                            responseBody
                        }
                        showError(tvError, msg.ifEmpty { getString(R.string.error_invalid_email_password) })
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.button_login)
                    showError(tvError, getString(R.string.error_connection_failed))
                }
            }
        }
    }

    private fun showError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = View.VISIBLE
    }

    private fun configurePasswordToggle(field: EditText) {
        field.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = field.compoundDrawablesRelative[2] ?: return@setOnTouchListener false
                if (event.rawX >= field.right - drawable.bounds.width() - field.paddingEnd) {
                    val isVisible = field.transformationMethod !is PasswordTransformationMethod
                    field.transformationMethod = if (isVisible) {
                        PasswordTransformationMethod.getInstance()
                    } else {
                        HideReturnsTransformationMethod.getInstance()
                    }
                    field.setSelection(field.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
