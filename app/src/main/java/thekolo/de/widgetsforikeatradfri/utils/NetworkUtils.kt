package thekolo.de.widgetsforikeatradfri.utils

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.net.Inet4Address
import java.net.NetworkInterface


object NetworkUtils {
    private const val GATEWAY_PREFIX = "GW-"

    fun getIpAddress(useIPv4: Boolean = true): String? {
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
        } catch (ex: Exception) {
        }

        return null
    }

    fun scanNetwork(deviceIp: String, onUpdate: (String, String?) -> Unit) {
        val baseAddress = deviceIp.split(".").take(3).joinToString(".")
        val addressRange = (0..255)

        val scans = addressRange.map { address ->
            async {
                val ip = "${baseAddress}.$address"
                val hostname = getHostname(ip)
                onUpdate(ip, hostname)
            }
        }

        return runBlocking {
            scans.forEach { it.join() }
        }
    }

    fun getHostname(ip: String): String? {
        try {
            return Inet4Address.getByName(ip).canonicalHostName
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

            //TODO: change
            scanNetwork("192.168.178.1") { ip, hostname ->
                currentCount++
                onProgressChanged(((currentCount / 256.0) * 100).toInt())

                if (hostname != null && hostname.startsWith(GATEWAY_PREFIX))
                    foundIp = ip
            }

            return@async foundIp
        }
    }
}