package thekolo.de.quicktilesforikeatradfri.utils

object ValidateUtil {
    private const val VALID_IP_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\$"

    fun isValidIp(ip: String): Boolean {
        return ip.matches(Regex(VALID_IP_REGEX)) || ip.isEmpty()
    }
}