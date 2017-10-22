package thekolo.de.widgetsforikeatradfri.utils

import android.content.Context

object SettingsUtil {
    const val DEFAULT_SHARED_PREFERENCES_FILE = "thekolo.de.widgetsforikeatradfri.settings"
    const val GATEWAY_IP_KEY = "gateway_ip"
    const val SECURITY_ID_KEY = "security_id"

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
}