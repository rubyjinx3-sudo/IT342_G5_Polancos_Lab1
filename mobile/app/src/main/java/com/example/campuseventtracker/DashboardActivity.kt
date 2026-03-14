package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class DashboardActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvRole = findViewById<TextView>(R.id.tvRole)
        val ivHeaderAvatar = findViewById<ImageView>(R.id.ivHeaderAvatar)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvNoEvents = findViewById<TextView>(R.id.tvNoEvents)
        val tvNoRegistered = findViewById<TextView>(R.id.tvNoRegistered)
        val containerUpcomingEvents = findViewById<LinearLayout>(R.id.containerUpcomingEvents)
        val containerRegisteredEvents = findViewById<LinearLayout>(R.id.containerRegisteredEvents)

        val sharedPref = getSharedPreferences(AppConfig.USER_PREFS, MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", null)
        val email = sharedPref.getString("email", null)
        val role = sharedPref.getString("role", "STUDENT")
        val userId = sharedPref.getLong("userId", -1L)

        if (email == null || userId <= 0L) {
            goToLogin()
            return
        }

        tvWelcome.text = if (fullName.isNullOrBlank()) {
            getString(R.string.dashboard_welcome_generic)
        } else {
            getString(R.string.dashboard_welcome_named, fullName)
        }
        tvRole.text = role?.lowercase()?.replaceFirstChar { it.uppercase() }
            ?: getString(R.string.dashboard_role_fallback)
        updateHeaderAvatar(ivHeaderAvatar, ProfilePreferences.getAvatarUri(this, email))

        progressBar.visibility = View.VISIBLE
        loadDashboard(
            userId = userId,
            progressBar = progressBar,
            tvNoEvents = tvNoEvents,
            tvNoRegistered = tvNoRegistered,
            containerUpcomingEvents = containerUpcomingEvents,
            containerRegisteredEvents = containerRegisteredEvents
        )

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            goToLogin()
        }
    }

    private fun loadDashboard(
        userId: Long,
        progressBar: ProgressBar,
        tvNoEvents: TextView,
        tvNoRegistered: TextView,
        containerUpcomingEvents: LinearLayout,
        containerRegisteredEvents: LinearLayout
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eventsBody = fetchJson("${AppConfig.BASE_URL}/events")
                val registrationsBody = fetchJson("${AppConfig.BASE_URL}/registrations/user/$userId")

                val allEvents = JSONArray(eventsBody)
                val registrations = JSONArray(registrationsBody)
                val registeredEventIds = mutableSetOf<Long>()

                for (i in 0 until registrations.length()) {
                    registeredEventIds.add(registrations.getJSONObject(i).optLong("eventId"))
                }

                val upcomingEvents = mutableListOf<EventUiItem>()
                val registeredEvents = mutableListOf<EventUiItem>()

                for (i in 0 until allEvents.length()) {
                    val event = allEvents.getJSONObject(i)
                    val item = EventUiItem(
                        id = event.optLong("id"),
                        title = event.optString("title", "Untitled"),
                        date = event.optString("date", "Date TBD"),
                        location = event.optString("location", "Location TBD")
                    )

                    upcomingEvents.add(item)
                    if (registeredEventIds.contains(item.id)) {
                        registeredEvents.add(item)
                    }
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvNoEvents.visibility = if (upcomingEvents.isEmpty()) View.VISIBLE else View.GONE
                    tvNoRegistered.visibility = if (registeredEvents.isEmpty()) View.VISIBLE else View.GONE

                    populateEventSection(containerUpcomingEvents, upcomingEvents.take(6))
                    populateEventSection(containerRegisteredEvents, registeredEvents)
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    tvNoEvents.text = getString(R.string.dashboard_error_connection)
                    tvNoEvents.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun populateEventSection(container: LinearLayout, items: List<EventUiItem>) {
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        items.forEach { item ->
            val card = inflater.inflate(R.layout.item_event_card, container, false)
            card.findViewById<TextView>(R.id.tvEventTitle).text = item.title
            card.findViewById<TextView>(R.id.tvEventDate).text = item.date
            card.findViewById<TextView>(R.id.tvEventLocation).text = item.location
            container.addView(card)
        }
    }

    private fun fetchJson(urlString: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.connectTimeout = 8000
        conn.readTimeout = 8000
        conn.connect()
        val body = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        return body
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun updateHeaderAvatar(imageView: ImageView, uriString: String?) {
        val iconPadding = (6 * resources.displayMetrics.density).toInt()
        if (uriString.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.ic_campus_logo)
            imageView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            return
        }

        imageView.setPadding(0, 0, 0, 0)
        imageView.setImageURI(Uri.parse(uriString))
    }
}

private data class EventUiItem(
    val id: Long,
    val title: String,
    val date: String,
    val location: String
)
