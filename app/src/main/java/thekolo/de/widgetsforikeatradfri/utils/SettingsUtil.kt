package thekolo.de.widgetsforikeatradfri.utils

import android.content.Context

object SettingsUtil {
    const val DEFAULT_SHARED_PREFERENCES_FILE = "thekolo.de.widgetsforikeatradfri.settings"
    const val GATEWAY_IP_KEY = "gateway_ip"
    const val SECURITY_ID_KEY = "security_id"
    const val ON_BOARDING_COMPLETED_KEY = "on_boarding_completed"

    fun getGatewayIp(context: Context): String? {
        return context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(GATEWAY_IP_KEY, null)
    }

    fun setGatewayIp(context: Context, ip: String) {
        val prefsEdit = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
        prefsEdit.putString(GATEWAY_IP_KEY, ip)
        prefsEdit.apply()
    }

    fun getSecurityId(context: Context): String? {
        return context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(SECURITY_ID_KEY, null)
    }

    fun setSecurityId(context: Context, id: String) {
        val prefsEdit = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
        prefsEdit.putString(SECURITY_ID_KEY, id)
        prefsEdit.apply()
    }

    fun getOnboardingCompleted(context: Context): Boolean {
        return context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getBoolean(ON_BOARDING_COMPLETED_KEY, false)
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean) {
        val prefsEdit = context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
        prefsEdit.putBoolean(ON_BOARDING_COMPLETED_KEY, completed)
        prefsEdit.apply()
    }
}