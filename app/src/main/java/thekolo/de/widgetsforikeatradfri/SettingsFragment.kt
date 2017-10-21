package thekolo.de.widgetsforikeatradfri

import android.content.SharedPreferences
import android.preference.PreferenceFragment
import android.os.Bundle
import android.preference.EditTextPreference
import android.R.attr.key





class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = DEFAULT_SHARED_PREFERENCES_FILE

        addPreferencesFromResource(R.xml.settings)
        val sharedPreferences = preferenceManager.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        updateSummaries()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(sharedPreferences == null || key == null) return

        updateSummaries()
    }

    private fun updateSummaries() {
        val gatewayPref = preferenceManager.findPreference("gateway_ip") as EditTextPreference
        gatewayPref.summary = gatewayPref.text

        val securityIdPref = preferenceManager.findPreference("security_id") as EditTextPreference
        securityIdPref.summary = securityIdPref.text
    }

    companion object {
        const val DEFAULT_SHARED_PREFERENCES_FILE = "thekolo.de.widgetsforikeatradfri.settings"
    }
}