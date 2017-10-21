package thekolo.de.widgetsforikeatradfri.utils

import android.content.Context

object SettingsUtil {
    const val DEFAULT_SHARED_PREFERENCES_FILE = "thekolo.de.widgetsforikeatradfri.settings"
    const val GATEWAY_IP_KEY = "gateway_ip"
    const val SECURITY_ID_KEY = "security_id"

    fun getGatewayIp(context: Context): String? {
        return context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(GATEWAY_IP_KEY, null)
    }

    fun getSecurityId(context: Context): String? {
        return context.getSharedPreferences(DEFAULT_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).getString(SECURITY_ID_KEY, null)
    }
}