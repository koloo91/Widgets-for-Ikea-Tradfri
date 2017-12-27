package thekolo.de.quicktilesforikeatradfri.utils

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
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
        } catch (ex: Exception) {
        }

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

    fun searchGatewayIp(onSucces: (String) -> Unit, onError: () -> Unit, onDeviceFound: (String) -> Unit, onProgressChanged: (Int) -> Unit) {
        launch(CommonPool) {
            //TODO: Uncomment
            val deviceIp = "192.168.178.44"//getIpAddress()
            if (deviceIp == null) {
                onError()
                return@launch
            }

            var currentCount = 0

            scanNetwork(deviceIp) { ip, hostname ->
                currentCount++

                launch(UI) {
                    if(ip != hostname)
                        onDeviceFound("$ip $hostname")
                    onProgressChanged(((currentCount / 256.0) * 100).toInt())
                }

                if (hostname != null && hostname.startsWith(GATEWAY_PREFIX)) {
                    println(ip)
                    launch(UI) { onSucces(ip) }
                }
            }
        }
    }
}