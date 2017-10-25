package thekolo.de.widgetsforikeatradfri.utils

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.net.InetAddress
import java.net.NetworkInterface


object NetworkUtils {
    private const val GATEWAY_PREFIX = "GW-"

    private fun getIpAddress(useIPv4: Boolean = true): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces().toList()
            interfaces.forEach { iface ->
                val addresses = iface.inetAddresses.toList()
                addresses.forEach { address ->
                    if (address.isLoopbackAddress) return@forEach
                    val hostAddress = address.hostAddress
                    val isIPv4 = hostAddress.indexOf(":") < 0
                    if (useIPv4 && isIPv4) {
                        return hostAddress
                    }
                }
            }
        } catch (ex: Exception) {}

        return null
    }

    private fun scanNetwork(deviceIp: String, onUpdate: (String, String?) -> Unit) {
        val baseAddress = deviceIp.split(".").take(3).joinToString(".")
        val addressRange = (0..255)

        addressRange.map { address ->
            val ip = "$baseAddress.$address"
            val hostname = getHostname(ip)
            onUpdate(ip, hostname)
        }
    }

    private fun getHostname(ip: String): String? {
        try {
            return InetAddress.getByName(ip).canonicalHostName
        } catch (e: Exception) {
            println(e)
        }
        return null
    }

    fun searchGatewayIp(onProgressChanged: (Int) -> Unit): Deferred<String?> {
        return async {
            val deviceIp = getIpAddress() ?: return@async null
            var foundIp: String? = null
            var currentCount = 0

            scanNetwork(deviceIp) { ip, hostname ->
                currentCount++
                onProgressChanged(((currentCount / 256.0) * 100).toInt())

                if (hostname != null && hostname.startsWith(GATEWAY_PREFIX)) {
                    println(foundIp)
                    foundIp = ip
                }
            }

            return@async foundIp
        }
    }
}