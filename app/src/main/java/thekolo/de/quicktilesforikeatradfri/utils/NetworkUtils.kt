package thekolo.de.quicktilesforikeatradfri.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.NetworkInterface


object NetworkUtils {
    private const val GATEWAY_PREFIX = "GW-"

    val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "MainActivity", Log.getStackTraceString(ex))
    }

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

    private fun scanNetwork(deviceIp: String, onUpdate: (String, String?) -> Unit, finished: () -> Unit) {
        val baseAddress = deviceIp.split(".").take(3).joinToString(".")
        val addressRange = (0..255)

        addressRange.map { address ->
            val ip = "$baseAddress.$address"
            val hostname = getHostname(ip)
            onUpdate(ip, hostname)
        }

        finished()
    }

    private fun getHostname(ip: String): String? {
        try {
            return InetAddress.getByName(ip).canonicalHostName
        } catch (e: Exception) {
            println(e)
        }
        return null
    }

    fun searchGatewayIp(onSuccess: (String) -> Unit, onError: () -> Unit, onDeviceFound: (Pair<String, String>) -> Unit, onProgressChanged: (Int) -> Unit, onFinished: () -> Unit) {
        CoroutineScope(Dispatchers.Default + handler).launch {
            val deviceIp = getIpAddress()
            if (deviceIp == null) {
                onError()
                return@launch
            }

            var currentCount = 0

            scanNetwork(deviceIp, { ip, hostname ->
                currentCount++

                CoroutineScope(Dispatchers.Main + handler).launch {
                    if (ip != hostname)
                        onDeviceFound(Pair(ip, hostname ?: ""))
                    onProgressChanged(((currentCount / 256.0) * 100).toInt())
                }

                if (hostname != null && hostname.startsWith(GATEWAY_PREFIX)) {
                    println(ip)
                    CoroutineScope(Dispatchers.Main + handler).launch { onSuccess(ip) }
                }
            }, {
                CoroutineScope(Dispatchers.Main + handler).launch {
                    onFinished()
                }
            })
        }
    }
}