package com.example.campuseventtracker

import android.app.Activity
import android.graphics.BitmapFactory
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class DashboardActivity : Activity() {
    private val allUpcomingEvents = mutableListOf<EventUiItem>()
    private val allRegisteredEvents = mutableListOf<EventUiItem>()

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
        val etSearch = findViewById<EditText>(R.id.etEventSearch)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategoryFilter)
        val spinnerDepartment = findViewById<Spinner>(R.id.spinnerDepartmentFilter)
        val etDate = findViewById<EditText>(R.id.etDateFilter)
        val btnResetFilters = findViewById<Button>(R.id.btnResetFilters)
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

        setupCategorySpinner(spinnerCategory)
        setupDepartmentSpinner(spinnerDepartment)
        setupFilterListeners(
            etSearch = etSearch,
            spinnerCategory = spinnerCategory,
            spinnerDepartment = spinnerDepartment,
            etDate = etDate,
            btnResetFilters = btnResetFilters,
            tvNoEvents = tvNoEvents,
            tvNoRegistered = tvNoRegistered,
            containerUpcomingEvents = containerUpcomingEvents,
            containerRegisteredEvents = containerRegisteredEvents
        )

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
                        location = event.optString("location", "Location TBD"),
                        category = event.optString("category", inferCategory(event.optString("title", ""))),
                        department = event.optString("department", ""),
                        imageUrl = event.optString("imageUrl", "")
                    )

                    upcomingEvents.add(item)
                    if (registeredEventIds.contains(item.id)) {
                        registeredEvents.add(item)
                    }
                }

                withContext(Dispatchers.Main) {
                    allUpcomingEvents.clear()
                    allUpcomingEvents.addAll(upcomingEvents)
                    allRegisteredEvents.clear()
                    allRegisteredEvents.addAll(registeredEvents)
                    progressBar.visibility = View.GONE
                    applyFilters(
                        tvNoEvents = tvNoEvents,
                        tvNoRegistered = tvNoRegistered,
                        containerUpcomingEvents = containerUpcomingEvents,
                        containerRegisteredEvents = containerRegisteredEvents
                    )
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

    private fun setupCategorySpinner(spinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.dashboard_category_filter_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupDepartmentSpinner(spinner: Spinner) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.dashboard_department_filter_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupFilterListeners(
        etSearch: EditText,
        spinnerCategory: Spinner,
        spinnerDepartment: Spinner,
        etDate: EditText,
        btnResetFilters: Button,
        tvNoEvents: TextView,
        tvNoRegistered: TextView,
        containerUpcomingEvents: LinearLayout,
        containerRegisteredEvents: LinearLayout
    ) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                applyFilters(tvNoEvents, tvNoRegistered, containerUpcomingEvents, containerRegisteredEvents)
            }
        }

        etSearch.addTextChangedListener(watcher)
        etDate.addTextChangedListener(watcher)
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters(tvNoEvents, tvNoRegistered, containerUpcomingEvents, containerRegisteredEvents)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        spinnerCategory.onItemSelectedListener = spinnerListener
        spinnerDepartment.onItemSelectedListener = spinnerListener

        btnResetFilters.setOnClickListener {
            etSearch.text.clear()
            etDate.text.clear()
            spinnerCategory.setSelection(0)
            spinnerDepartment.setSelection(0)
        }
    }

    private fun applyFilters(
        tvNoEvents: TextView,
        tvNoRegistered: TextView,
        containerUpcomingEvents: LinearLayout,
        containerRegisteredEvents: LinearLayout
    ) {
        val search = findViewById<EditText>(R.id.etEventSearch).text.toString().trim().lowercase()
        val date = findViewById<EditText>(R.id.etDateFilter).text.toString().trim()
        val category = when (findViewById<Spinner>(R.id.spinnerCategoryFilter).selectedItemPosition) {
            1 -> "academic"
            2 -> "cultural"
            3 -> "career"
            4 -> "sports"
            5 -> "technology"
            6 -> "social"
            else -> "all"
        }
        val department = when (findViewById<Spinner>(R.id.spinnerDepartmentFilter).selectedItemPosition) {
            1 -> "College of Engineering and Architecture"
            2 -> "College of Management, Business and Accountancy"
            3 -> "College of Arts, Science, and Education"
            4 -> "College of Nursing and Allied Health Sciences"
            5 -> "College of Computer Studies"
            6 -> "College of Criminal Justice"
            7 -> "All Colleges"
            else -> "all"
        }

        val filteredUpcoming = allUpcomingEvents.filter { matchesFilters(it, search, category, department, date) }
        val filteredRegistered = allRegisteredEvents.filter { matchesFilters(it, search, category, department, date) }

        populateEventSection(containerUpcomingEvents, filteredUpcoming)
        populateEventSection(containerRegisteredEvents, filteredRegistered)

        tvNoEvents.text = if (hasActiveFilters(search, category, date)) {
            getString(R.string.dashboard_no_events_filtered)
        } else {
            getString(R.string.dashboard_no_events)
        }
        tvNoRegistered.text = if (hasActiveFilters(search, category, date)) {
            getString(R.string.dashboard_no_registered_filtered)
        } else {
            getString(R.string.dashboard_no_registered)
        }

        tvNoEvents.visibility = if (filteredUpcoming.isEmpty()) View.VISIBLE else View.GONE
        tvNoRegistered.visibility = if (filteredRegistered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun hasActiveFilters(search: String, category: String, date: String): Boolean {
        val department = when (findViewById<Spinner>(R.id.spinnerDepartmentFilter).selectedItemPosition) {
            0 -> "all"
            7 -> "All Colleges"
            else -> "filtered"
        }
        return search.isNotBlank() || category != "all" || date.isNotBlank() || department != "all"
    }

    private fun matchesFilters(item: EventUiItem, search: String, category: String, department: String, date: String): Boolean {
        val matchesSearch = search.isBlank() ||
            item.title.lowercase().contains(search) ||
            item.location.lowercase().contains(search) ||
            item.category.lowercase().contains(search) ||
            item.department.lowercase().contains(search)

        val matchesCategory = category == "all" || item.category == category
        val matchesDepartment = department == "all" || item.department == department
        val matchesDate = date.isBlank() || item.date.startsWith(date)

        return matchesSearch && matchesCategory && matchesDepartment && matchesDate
    }

    private fun inferCategory(title: String): String {
        return when {
            Regex("tech|hack|science|code|program|robot|innovation|digital|ict", RegexOption.IGNORE_CASE).containsMatchIn(title) -> "technology"
            Regex("academic|research|seminar|workshop|quiz|forum|lecture", RegexOption.IGNORE_CASE).containsMatchIn(title) -> "academic"
            Regex("cultur|music|art|festival|dance|perform", RegexOption.IGNORE_CASE).containsMatchIn(title) -> "cultural"
            Regex("career|fair|job|summit|entrepreneur|business", RegexOption.IGNORE_CASE).containsMatchIn(title) -> "career"
            Regex("sport|game|tournament|athletic", RegexOption.IGNORE_CASE).containsMatchIn(title) -> "sports"
            else -> "social"
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
            card.findViewById<TextView>(R.id.tvEventDepartment).text = item.department.ifBlank { "All Colleges" }
            bindEventImage(
                imageView = card.findViewById(R.id.ivEventImage),
                placeholderView = card.findViewById(R.id.ivEventPlaceholder),
                imageUrl = item.imageUrl
            )
            container.addView(card)
        }
    }

    private fun bindEventImage(imageView: ImageView, placeholderView: ImageView, imageUrl: String?) {
        if (imageUrl.isNullOrBlank()) {
            imageView.setImageDrawable(null)
            placeholderView.visibility = View.VISIBLE
            return
        }

        placeholderView.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(imageUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.doInput = true
                connection.connect()
                val bitmap = BitmapFactory.decodeStream(connection.inputStream)
                connection.disconnect()

                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        placeholderView.visibility = View.GONE
                    } else {
                        imageView.setImageDrawable(null)
                        placeholderView.visibility = View.VISIBLE
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    imageView.setImageDrawable(null)
                    placeholderView.visibility = View.VISIBLE
                }
            }
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
    val location: String,
    val category: String,
    val department: String,
    val imageUrl: String
)
