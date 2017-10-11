package thekolo.de.widgetsforikeatradfri

import com.google.gson.Gson
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.network.config.NetworkConfig
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore
import java.lang.reflect.Type
import java.net.InetSocketAddress

class TradfriClient(private val ip: String, private val securityId: String) {
    private val gson = Gson()
    private val baseUrl = "coap://$ip:5684"

    private fun client(url: String): CoapClient {
        val client = CoapClient(url)

        val builder = DtlsConnectorConfig.Builder(InetSocketAddress(0))
        builder.setPskStore(StaticPskStore("Client_identity", securityId.toByteArray()))
        val dtlsConnector = DTLSConnector(builder.build())
        client.endpoint = CoapEndpoint(dtlsConnector, NetworkConfig.getStandard())

        return client
    }

    fun getDeviceIds(): List<Int>? {
        val response = client("$baseUrl/15001").get()
        return parseResponse(response, List::class.java) as List<Int>?
    }

    fun getDevice(deviceId: String): Device? {
        val response = client("$baseUrl/15001/$deviceId").get()
        return parseResponse(response, Device::class.java)
    }

    fun getDevices(): List<Device> {
        val deviceIds = getDeviceIds() ?: emptyList()
        return deviceIds.map {
            getDevice("$it")
        }.filter { it != null }.map { it!! }
    }

    fun getGroups(): CoapResponse {
        return client("$baseUrl/15004").get()
    }

    fun getGroup(groupId: String): CoapResponse {
        return client("$baseUrl/15004/$groupId").get()
    }

    fun turnDeviceOn(deviceId: String): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(1)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.TEXT_PLAIN)
    }

    fun turnDeviceOff(deviceId: String): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(0)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.TEXT_PLAIN)
    }

    private fun <T> parseResponse(response: CoapResponse, type: Class<T>): T? {
        try {
            val payload = String(response.payload)
            return gson.fromJson<T>(payload, type)
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }
}