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
import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.GroupUpdater
import java.net.InetSocketAddress


class TradfriClient(ip: String,
                    private val securityId: String,
                    var identity: String?,
                    var preSharedKey: String?) {

    private val baseUrl = "coaps://$ip:5684"

    private val gson = Gson()
    private var coapRegisterEndpoint: CoapEndpoint = getRegisterCoapEndpoint()
    private var coapEndpoint: CoapEndpoint = getDefaultCoapEndpoint()

    private val timeout = 2000L

    private fun getRegisterCoapEndpoint(): CoapEndpoint {
        val builder = DtlsConnectorConfig.Builder(InetSocketAddress(0))
        builder.setPskStore(StaticPskStore("Client_identity", securityId.toByteArray()))
        builder.setRetransmissionTimeout(50000)

        val dtlsConnector = DTLSConnector(builder.build())

        val network = NetworkConfig.createStandardWithoutFile()
        return CoapEndpoint(dtlsConnector, network)
    }

    private fun getDefaultCoapEndpoint(): CoapEndpoint {
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
        client.timeout = timeout
        return client
    }

    private fun client(url: String): CoapClient {
        val client = CoapClient(url)
        client.endpoint = coapEndpoint
        client.timeout = timeout
        return client
    }

    fun register(identity: String): CoapResponse? {
        Log.d(LogName, "POST $baseUrl/15011/9063")
        return registerClient("$baseUrl/15011/9063").post("{\"9090\": \"$identity\"}", MediaTypeRegistry.APPLICATION_JSON)
    }

    fun ping(): CoapResponse? {
        Log.d(LogName, "GET $baseUrl/.well-known/core")
        return client("$baseUrl/.well-known/core").get()
    }

    fun getDeviceIds(): CoapResponse? {
        Log.d(LogName, "GET $baseUrl/15001")
        return client("$baseUrl/15001").get()
    }

    fun getDevice(deviceId: Int): CoapResponse? {
        Log.d(LogName, "GET $baseUrl/15001/$deviceId")
        return client("$baseUrl/15001/$deviceId").get()
    }

    fun turnDeviceOn(deviceId: Int): CoapResponse? {
        Log.d(LogName, "PUT $baseUrl/15001/$deviceId")
        val updateData = DeviceUpdater(listOf(DeviceState(BulbState.On)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun turnDeviceOff(deviceId: Int): CoapResponse? {
        Log.d(LogName, "PUT $baseUrl/15001/$deviceId")
        val updateData = DeviceUpdater(listOf(DeviceState(BulbState.Off)))
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
        Log.d(LogName, "PUT $baseUrl/15004/$groupId")
        val updateData = GroupUpdater(BulbState.On)
        return client("$baseUrl/15004/$groupId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun turnGroupOff(groupId: Int): CoapResponse? {
        Log.d(LogName, "PUT $baseUrl/15004/$groupId")
        val updateData = GroupUpdater(BulbState.Off)
        return client("$baseUrl/15004/$groupId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun reload() {
        coapRegisterEndpoint = getRegisterCoapEndpoint()
        coapEndpoint = getDefaultCoapEndpoint()
    }

    companion object {
        const val LogName = "TradfriClient"
    }
}