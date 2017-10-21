package thekolo.de.widgetsforikeatradfri.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.PreferenceFragment
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil.DEFAULT_SHARED_PREFERENCES_FILE


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
        if (sharedPreferences == null || key == null) return

        updateSummaries()
    }

    private fun updateSummaries() {
        val gatewayPref = preferenceManager.findPreference(SettingsUtil.GATEWAY_IP_KEY) as EditTextPreference
        gatewayPref.summary = gatewayPref.text

        val securityIdPref = preferenceManager.findPreference(SettingsUtil.SECURITY_ID_KEY) as EditTextPreference
        securityIdPref.summary = securityIdPref.text
    }

}