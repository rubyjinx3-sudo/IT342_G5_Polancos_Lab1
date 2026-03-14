package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val spinnerYear = findViewById<Spinner>(R.id.spinnerYear)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvSuccess = findViewById<TextView>(R.id.tvSuccess)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        val years = listOf(
            getString(R.string.hint_select_year),
            getString(R.string.year_freshman),
            getString(R.string.year_sophomore),
            getString(R.string.year_junior),
            getString(R.string.year_senior),
            getString(R.string.year_graduate)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = adapter

        configurePasswordToggle(etPassword)
        configurePasswordToggle(etConfirmPassword)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val role = "STUDENT"

            when {
                fullName.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    showError(tvError, tvSuccess, getString(R.string.error_fill_required_fields))
                    return@setOnClickListener
                }
                fullName.length < 2 -> {
                    showError(tvError, tvSuccess, getString(R.string.error_name_min_length))
                    return@setOnClickListener
                }
                !email.contains("@") -> {
                    showError(tvError, tvSuccess, getString(R.string.error_valid_email))
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    showError(tvError, tvSuccess, getString(R.string.error_password_min_length))
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    showError(tvError, tvSuccess, getString(R.string.error_password_mismatch))
                    return@setOnClickListener
                }
            }

            tvError.visibility = View.GONE
            btnRegister.isEnabled = false
            btnRegister.text = getString(R.string.button_creating_account)

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
                val url = URL("${AppConfig.BASE_URL}/auth/register")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.doOutput = true

                val body = JSONObject().apply {
                    put("fullName", fullName)
                    put("email", email)
                    put("password", password)
                    put("role", role)
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
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.button_create_account)

                    if (code == 200) {
                        tvSuccess.text = getString(R.string.success_account_created)
                        tvSuccess.visibility = View.VISIBLE
                        tvError.visibility = View.GONE

                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1500)
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                    } else {
                        val msg = try {
                            JSONObject(responseBody).optString("message", responseBody)
                        } catch (_: Exception) {
                            responseBody
                        }
                        showError(tvError, tvSuccess, msg.ifEmpty { getString(R.string.error_connection_failed) })
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.button_create_account)
                    showError(tvError, tvSuccess, getString(R.string.error_connection_failed))
                }
            }
        }
    }

    private fun showError(tvError: TextView, tvSuccess: TextView, msg: String) {
        tvError.text = msg
        tvError.visibility = View.VISIBLE
        tvSuccess.visibility = View.GONE
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
}
