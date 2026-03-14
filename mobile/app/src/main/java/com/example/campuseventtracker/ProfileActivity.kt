package com.example.campuseventtracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ProfileActivity : Activity() {

    private var currentEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnChangePhoto = findViewById<Button>(R.id.btnChangePhoto)
        val ivProfileAvatar = findViewById<ImageView>(R.id.ivProfileAvatar)
        val tvProfileName = findViewById<TextView>(R.id.tvProfileName)
        val tvProfileEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvProfileRole = findViewById<TextView>(R.id.tvProfileRole)
        val tvProfileRegisteredCount = findViewById<TextView>(R.id.tvProfileRegisteredCount)
        val tvProfileMemberSince = findViewById<TextView>(R.id.tvProfileMemberSince)
        val tvProfileLastLogin = findViewById<TextView>(R.id.tvProfileLastLogin)

        val sharedPref = getSharedPreferences(AppConfig.USER_PREFS, MODE_PRIVATE)
        val fullName = sharedPref.getString("fullName", getString(R.string.profile_default_name))
            ?: getString(R.string.profile_default_name)
        val email = sharedPref.getString("email", "") ?: ""
        val role = sharedPref.getString("role", getString(R.string.profile_role_student))
            ?: getString(R.string.profile_role_student)
        val userId = sharedPref.getLong("userId", -1L)
        currentEmail = email

        tvProfileName.text = fullName
        tvProfileEmail.text = email
        tvProfileRole.text = role.lowercase().replaceFirstChar { it.uppercase() }
        tvProfileRegisteredCount.text = getString(R.string.profile_registered_events, 0)
        tvProfileMemberSince.text = getString(R.string.profile_member_since, getString(R.string.profile_not_available))
        tvProfileLastLogin.text = getString(R.string.profile_last_login, getString(R.string.profile_not_available))

        updateAvatar(ivProfileAvatar, ProfilePreferences.getAvatarUri(this, email))

        btnBack.setOnClickListener { finish() }
        btnChangePhoto.setOnClickListener { openPhotoPicker() }

        if (email.isNotBlank() && userId > 0L) {
            loadProfile(email, userId, tvProfileRegisteredCount, tvProfileMemberSince, tvProfileLastLogin)
        }
    }

    private fun loadProfile(
        email: String,
        userId: Long,
        tvRegisteredCount: TextView,
        tvMemberSince: TextView,
        tvLastLogin: TextView
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profileUrl = URL("${AppConfig.BASE_URL}/user/me?email=$email")
                val profileConn = profileUrl.openConnection() as HttpURLConnection
                profileConn.requestMethod = "GET"
                profileConn.connectTimeout = 8000
                profileConn.readTimeout = 8000
                val profileBody = profileConn.inputStream.bufferedReader().readText()
                profileConn.disconnect()

                val registrationsUrl = URL("${AppConfig.BASE_URL}/registrations/user/$userId")
                val registrationsConn = registrationsUrl.openConnection() as HttpURLConnection
                registrationsConn.requestMethod = "GET"
                registrationsConn.connectTimeout = 8000
                registrationsConn.readTimeout = 8000
                val registrationsBody = registrationsConn.inputStream.bufferedReader().readText()
                registrationsConn.disconnect()

                val profileJson = JSONObject(profileBody)
                val registrationsJson = JSONArray(registrationsBody)

                withContext(Dispatchers.Main) {
                    tvRegisteredCount.text = getString(R.string.profile_registered_events, registrationsJson.length())
                    tvMemberSince.text = getString(
                        R.string.profile_member_since,
                        profileJson.optString("createdAt", getString(R.string.profile_not_available))
                    )
                    tvLastLogin.text = getString(
                        R.string.profile_last_login,
                        profileJson.optString("lastLogin", getString(R.string.profile_not_available))
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    tvRegisteredCount.text = getString(R.string.profile_registered_events_unavailable)
                }
            }
        }
    }

    private fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_PICK_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != REQUEST_PICK_PHOTO || resultCode != RESULT_OK) return

        val uri = data?.data ?: return
        val flags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            contentResolver.takePersistableUriPermission(uri, flags)
        } catch (_: SecurityException) {
        }

        ProfilePreferences.saveAvatarUri(this, currentEmail, uri.toString())
        updateAvatar(findViewById(R.id.ivProfileAvatar), uri.toString())
    }

    private fun updateAvatar(imageView: ImageView, uriString: String?) {
        val iconPadding = (18 * resources.displayMetrics.density).toInt()

        if (uriString.isNullOrBlank()) {
            imageView.setImageResource(R.drawable.ic_campus_logo)
            imageView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            return
        }

        imageView.setPadding(0, 0, 0, 0)
        imageView.setImageURI(Uri.parse(uriString))
    }

    companion object {
        private const val REQUEST_PICK_PHOTO = 1001
    }
}
