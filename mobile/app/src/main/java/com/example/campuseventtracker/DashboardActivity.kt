package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.*
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class DashboardActivity : Activity() {

    private val BASE_URL = "http://192.168.254.103:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome   = findViewById<TextView>(R.id.tvWelcome)
        val tvRole      = findViewById<TextView>(R.id.tvRole)
        val btnLogout   = findViewById<Button>(R.id.btnLogout)
        val listEvents  = findViewById<ListView>(R.id.listEvents)
        val tvNoEvents  = findViewById<TextView>(R.id.tvNoEvents)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Get saved user from SharedPreferences
        val sharedPref = getSharedPreferences("ced_user", MODE_PRIVATE)
        val fullName   = sharedPref.getString("fullName", null)
        val email      = sharedPref.getString("email", null)
        val role       = sharedPref.getString("role", "STUDENT")

        // Not logged in → back to login
        if (email == null) {
            goToLogin()
            return
        }

        // Show welcome
        tvWelcome.text = "Welcome, $fullName!"
        tvRole.text    = role?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Student"

        // Load events
        progressBar.visibility = View.VISIBLE
        loadEvents(listEvents, tvNoEvents, progressBar)

        // Logout
        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            goToLogin()
        }
    }

    private fun loadEvents(
        listEvents: ListView,
        tvNoEvents: TextView,
        progressBar: ProgressBar
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url  = URL("$BASE_URL/events")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout    = 8000
                conn.connect()

                val code         = conn.responseCode
                val responseBody = if (code == 200)
                    conn.inputStream.bufferedReader().readText()
                else ""
                conn.disconnect()

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (code == 200 && responseBody.isNotEmpty()) {
                        val jsonArray = JSONArray(responseBody)
                        val eventList = mutableListOf<String>()

                        for (i in 0 until jsonArray.length()) {
                            val ev    = jsonArray.getJSONObject(i)
                            val title = ev.optString("title", "Untitled")
                            val date  = ev.optString("date",  "")
                            val loc   = ev.optString("location", "")
                            eventList.add("$title\n$date  •  $loc")
                        }

                        if (eventList.isEmpty()) {
                            tvNoEvents.visibility = View.VISIBLE
                            listEvents.visibility = View.GONE
                        } else {
                            tvNoEvents.visibility = View.GONE
                            listEvents.visibility = View.VISIBLE
                            val adapter = ArrayAdapter(
                                this@DashboardActivity,
                                android.R.layout.simple_list_item_1,
                                eventList
                            )
                            listEvents.adapter = adapter
                        }
                    } else {
                        tvNoEvents.text = "Could not load events."
                        tvNoEvents.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvNoEvents.text = "Connection failed. Is the server running?"
                    tvNoEvents.visibility = View.VISIBLE
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