package thekolo.de.widgetsforikeatradfri

import com.google.gson.Gson
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.network.config.NetworkConfig
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore
import java.net.InetSocketAddress

class TradfriClient(ip: String, private val securityId: String) {
    private val gson = Gson()
    private val baseUrl = "coap://$ip:5684"


    private fun getCoapEndpoint(): CoapEndpoint {
        println("GetCoapEndpoint")
        val builder = DtlsConnectorConfig.Builder(InetSocketAddress(0))
        builder.setPskStore(StaticPskStore("Client_identity", securityId.toByteArray()))
        val dtlsConnector = DTLSConnector(builder.build())
        return CoapEndpoint(dtlsConnector, NetworkConfig.getStandard())
    }

    private val coapEndpoint: CoapEndpoint = getCoapEndpoint()

    private fun <T> tryAsync(f: suspend () -> T): Deferred<T?> {
        return async {
            return@async try {
                f()
            } catch (e: Exception) {
                println(e)
                null
            }
        }
    }

    private fun client(url: String): CoapClient {
        val client = CoapClient(url)
        client.endpoint = coapEndpoint
        return client
    }

    private fun getDeviceIds(): Deferred<List<Int>?> {
        return tryAsync {
            val response = client("$baseUrl/15001").get()
            parseResponse(response, List::class.java) as List<Int>?
        }
    }

    fun getDevice(deviceId: Int): Deferred<Device?> {
        return tryAsync {
            val response = client("$baseUrl/15001/$deviceId").get()
            parseResponse(response, Device::class.java)
        }
    }

    fun getDevices(): Deferred<List<Device>?> {
        return tryAsync {
            val deviceIds = getDeviceIds().await() ?: emptyList()
            val devices = deviceIds.map { getDevice(it).await() }
            devices.filterNotNull().filter { !it.type.name.contains("remote control") }
        }
    }

    fun toogleDevice(deviceId: Int): Deferred<Unit?> {
        return tryAsync {
            val device = getDevice(deviceId).await() ?: return@tryAsync

            if (device.states != null && device.states.isNotEmpty()) {
                val state = device.states.first().on ?: 0
                when (state) {
                    0 -> turnDeviceOn(deviceId)
                    1 -> turnDeviceOff(deviceId)
                    else -> turnDeviceOn(deviceId)
                }
            } else {
                turnDeviceOn(deviceId)
            }
        }
    }

    fun getGroups(): CoapResponse {
        return client("$baseUrl/15004").get()
    }

    fun getGroup(groupId: String): CoapResponse {
        return client("$baseUrl/15004/$groupId").get()
    }

    fun turnDeviceOn(deviceId: Int): Deferred<CoapResponse?> {
        return tryAsync {
            val updateData = DeviceUpdater(listOf(DeviceState(1)))
            client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.TEXT_PLAIN)
        }
    }

    fun turnDeviceOff(deviceId: Int): Deferred<CoapResponse?> {
        return tryAsync {
            val updateData = DeviceUpdater(listOf(DeviceState(0)))
            client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.TEXT_PLAIN)
        }
    }

    private fun <T> parseResponse(response: CoapResponse, type: Class<T>): T? {
        return try {
            val payload = String(response.payload)
            gson.fromJson<T>(payload, type)
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}