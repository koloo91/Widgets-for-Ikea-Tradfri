package thekolo.de.quicktilesforikeatradfri.tradfri

import android.util.Log
import com.google.gson.Gson
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.network.config.NetworkConfig
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore
import thekolo.de.quicktilesforikeatradfri.DeviceState
import thekolo.de.quicktilesforikeatradfri.DeviceUpdater
import thekolo.de.quicktilesforikeatradfri.models.GroupUpdater
import java.net.InetSocketAddress


class TradfriClient(ip: String,
                    private val securityId: String,
                    var identity: String?,
                    var preSharedKey: String?) {

    private val baseUrl = "coaps://$ip:5684"

    private val gson = Gson()
    private val coapRegisterEndpoint: CoapEndpoint = getCoapRegisterEndpoint()
    private val coapEndpoint: CoapEndpoint = getCoapEndpoint()

    private fun getCoapRegisterEndpoint(): CoapEndpoint {
        val builder = DtlsConnectorConfig.Builder(InetSocketAddress(0))
        builder.setPskStore(StaticPskStore("Client_identity", securityId.toByteArray()))
        builder.setRetransmissionTimeout(50000)

        val dtlsConnector = DTLSConnector(builder.build())

        val network = NetworkConfig.createStandardWithoutFile()
        return CoapEndpoint(dtlsConnector, network)
    }

    private fun getCoapEndpoint(): CoapEndpoint {
        val builder = DtlsConnectorConfig.Builder(InetSocketAddress(0))
        builder.setPskStore(StaticPskStore(identity ?: "", (preSharedKey ?: "").toByteArray()))
        builder.setRetransmissionTimeout(50000)

        val dtlsConnector = DTLSConnector(builder.build())

        val network = NetworkConfig.createStandardWithoutFile()

        return CoapEndpoint(dtlsConnector, network)
    }

    private fun registerClient(url: String): CoapClient {
        val client = CoapClient(url)
        client.endpoint = coapRegisterEndpoint
        client.timeout = 6000
        return client
    }

    private fun client(url: String): CoapClient {
        val client = CoapClient(url)
        client.endpoint = coapEndpoint
        client.timeout = 6000
        return client
    }

    fun register(identity: String): CoapResponse? {
        return registerClient("$baseUrl/15011/9063").post("{\"9090\": \"$identity\"}", MediaTypeRegistry.APPLICATION_JSON)
    }

    fun ping(): CoapResponse? {
        return client("$baseUrl/.well-known/core").get()
    }

    fun getDeviceIds(): CoapResponse? {
        return client("$baseUrl/15001").get()
    }

    fun getDevice(deviceId: Int): CoapResponse? {
        return client("$baseUrl/15001/$deviceId").get()
    }

    fun turnDeviceOn(deviceId: Int): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(1)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun turnDeviceOff(deviceId: Int): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(0)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun getGroupIds(): CoapResponse? {
        Log.d(LogName, "GET $baseUrl/15004")
        return client("$baseUrl/15004").get()
    }

    fun getGroup(groupId: Int): CoapResponse? {
        Log.d(LogName, "GET $baseUrl/15004/$groupId")
        return client("$baseUrl/15004/$groupId").get()
    }

    fun turnGroupOn(groupId: Int): CoapResponse? {
        val updateData = GroupUpdater(1)
        return client("$baseUrl/15004/$groupId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun turnGroupOff(groupId: Int): CoapResponse? {
        val updateData = GroupUpdater(0)
        return client("$baseUrl/15004/$groupId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    companion object {
        const val LogName = "TradfriClient"
    }
}