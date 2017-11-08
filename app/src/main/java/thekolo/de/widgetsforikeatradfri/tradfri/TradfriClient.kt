package thekolo.de.widgetsforikeatradfri.tradfri

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResponse
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.network.CoapEndpoint
import org.eclipse.californium.core.network.config.NetworkConfig
import org.eclipse.californium.scandium.DTLSConnector
import org.eclipse.californium.scandium.config.DtlsConnectorConfig
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore
import thekolo.de.widgetsforikeatradfri.Device
import thekolo.de.widgetsforikeatradfri.DeviceState
import thekolo.de.widgetsforikeatradfri.DeviceUpdater
import thekolo.de.widgetsforikeatradfri.models.RegisterResult
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil
import java.net.InetSocketAddress
import java.util.*


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
        builder.setPskStore(StaticPskStore(identity!!, (preSharedKey!!).toByteArray()))
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

    fun getDeviceIds(): CoapResponse? {
        return client("$baseUrl/15001").get()
        //parseResponse(response, List::class.java) as List<Int>
    }

    fun ping(): CoapResponse? {
        return client("$baseUrl/.well-known/core").get()
    }

    fun getDevice(deviceId: Int): CoapResponse? {
        return client("$baseUrl/15001/$deviceId").get()
    }

    fun getGroups(): CoapResponse? {
        return client("$baseUrl/15004").get()
    }

    fun getGroup(groupId: String): CoapResponse? {
        return client("$baseUrl/15004/$groupId").get()
    }

    fun turnDeviceOn(deviceId: Int): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(1)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }

    fun turnDeviceOff(deviceId: Int): CoapResponse? {
        val updateData = DeviceUpdater(listOf(DeviceState(0)))
        return client("$baseUrl/15001/$deviceId").put(gson.toJson(updateData), MediaTypeRegistry.APPLICATION_JSON)
    }
}