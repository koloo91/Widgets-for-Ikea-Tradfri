package thekolo.de.quicktilesforikeatradfri.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.widget.Toast
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil.DEFAULT_SHARED_PREFERENCES_FILE
import thekolo.de.quicktilesforikeatradfri.utils.ValidateUtil


class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = DEFAULT_SHARED_PREFERENCES_FILE

        addPreferencesFromResource(R.xml.settings)
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        updateSummaries()
        displayVersion()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null || key == null) return

        try {
            updateSummaries()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun updateSummaries() {
        if(activity == null) return

        val gatewayPref = preferenceManager.findPreference(SettingsUtil.GATEWAY_IP_KEY) as EditTextPreference
        gatewayPref.summary = gatewayPref.text
        gatewayPref.setOnPreferenceChangeListener { preference, newValue ->
            val ip = newValue as String

            val result = ValidateUtil.isValidIp(ip)
            if(!result) {
                Toast.makeText(activity, "Please enter a valid IP address like 192.168.178.56", Toast.LENGTH_LONG).show()
            }

            result
        }

        val securityIdPref = preferenceManager.findPreference(SettingsUtil.SECURITY_ID_KEY) as EditTextPreference
        securityIdPref.summary = securityIdPref.text

        val identityPref = preferenceManager.findPreference(SettingsUtil.IDENTITY_KEY) as Preference
        identityPref.summary = SettingsUtil.getIdentity(activity) ?: ""

        val preSharedKeyPref = preferenceManager.findPreference(SettingsUtil.PRESHARED_KEY_KEY) as Preference
        preSharedKeyPref.summary = SettingsUtil.getPreSharedKey(activity) ?: ""
    }

    private fun displayVersion() {
        val info = activity.packageManager.getPackageInfo(activity.packageName, 0)
        preferenceManager.findPreference("version").summary = info.versionName
    }

    companion object {
        private const val VALID_IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"
    }
}