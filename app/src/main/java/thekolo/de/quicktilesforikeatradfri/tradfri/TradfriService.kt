package thekolo.de.quicktilesforikeatradfri.tradfri

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.eclipse.californium.core.CoapResponse
import thekolo.de.quicktilesforikeatradfri.Device
import thekolo.de.quicktilesforikeatradfri.models.Group
import thekolo.de.quicktilesforikeatradfri.models.RegisterResult
import thekolo.de.quicktilesforikeatradfri.services.QueueService
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil

class TradfriService(context: Context) {
    private val client: TradfriClient
    private val gson = Gson()
    private val queueService = QueueService.instance()

    private val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "TradfriService", Log.getStackTraceString(ex))
    }

    init {
        val gatewayIp = SettingsUtil.getGatewayIp(context) ?: ""
        val securityId = SettingsUtil.getSecurityId(context) ?: ""
        val identity = SettingsUtil.getIdentity(context)
        val preSharedKey = SettingsUtil.getPreSharedKey(context)

        client = TradfriClient(gatewayIp, securityId, identity, preSharedKey)
    }

    fun isRegistered(context: Context): Boolean {
        val identity = SettingsUtil.getIdentity(context)
        val preSharedKey = SettingsUtil.getPreSharedKey(context)

        return identity != null && identity.isNotEmpty() && preSharedKey != null && preSharedKey.isNotEmpty()
    }

    fun register(identity: String, onSuccess: (RegisterResult) -> Unit, onError: () -> Unit) {
        launch(CommonPool + handler) {
            val response = client.register(identity)

            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            val result = parseResponse(response, RegisterResult::class.java)
            if (result == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess(result) }
        }
    }

    fun ping(onSuccess: (String) -> Unit, onError: () -> Unit) {
        queueService.addAction({
            return@addAction _ping(onSuccess, onError)
        })
    }

    private fun _ping(onSuccess: (String) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            println("_ping start")
            val response = client.ping()
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess(String(response.payload)) }
            println("_ping end")
        }
    }

    private fun getDeviceIds(): List<Int> {
        val response = client.getDeviceIds() ?: return emptyList()

        if (!response.isSuccess)
            return emptyList()

        return parseResponse(response, List::class.java) as List<Int>? ?: return emptyList()
    }

    private fun getDevice(id: Int): Device? {
        val response = client.getDevice(id) ?: return null

        if (!response.isSuccess)
            return null

        return parseResponse(response, Device::class.java)
    }

    fun getDevice(id: Int, onSuccess: (Device) -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _getDevice(id, onSuccess, onError)
        }
    }

    private fun _getDevice(id: Int, onSuccess: (Device) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.getDevice(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            val result = parseResponse(response, Device::class.java)
            if (result == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess(result) }
        }
    }

    fun getDevices(onSuccess: (List<Device>) -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _getDevices(onSuccess, onError)
        }
    }

    private fun _getDevices(onSuccess: (List<Device>) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val deviceIds = getDeviceIds()

            val devices = deviceIds.mapNotNull { id ->
                getDevice(id)
            }.filter { device ->
                !device.type.name.contains("remote control")
            }

            launch(UI + handler) { onSuccess(devices) }
        }
    }

    fun turnDeviceOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _turnDeviceOn(id, onSuccess, onError)
        }
    }

    private fun _turnDeviceOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.turnDeviceOn(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess() }
        }
    }

    fun turnDeviceOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _turnDeviceOff(id, onSuccess, onError)
        }
    }

    private fun _turnDeviceOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.turnDeviceOff(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess() }
        }
    }

    fun toggleDevice(deviceId: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _toggleDevice(deviceId, onSuccess, onError)
        }
    }

    private fun _toggleDevice(deviceId: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val device = getDevice(deviceId)
            if (device == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (device.states != null && device.states.isNotEmpty()) {
                val state = device.states.first().on ?: 0
                when (state) {
                    0 -> turnDeviceOn(deviceId, onSuccess, onError)
                    1 -> turnDeviceOff(deviceId, onSuccess, onError)
                    else -> turnDeviceOn(deviceId, onSuccess, onError)
                }
            } else {
                turnDeviceOn(deviceId, onSuccess, onError)
            }
        }
    }

    private fun getGroupIds(): List<Int> {
        Log.d(LogName, "getGroupIds")
        val response = client.getGroupIds() ?: return emptyList()

        if (!response.isSuccess) {
            Log.d(LogName, "getGroupIds was not successful")
            return emptyList()
        }

        Log.d(LogName, "getGroupIds was successful -> found ${String(response.payload)}")
        return parseResponse(response, List::class.java) as List<Int>? ?: return emptyList()
    }

    fun getGroup(id: Int, onSuccess: (Group) -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _getGroup(id, onSuccess, onError)
        }
    }

    private fun _getGroup(id: Int, onSuccess: (Group) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.getGroup(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            val result = parseResponse(response, Group::class.java)
            if (result == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess(result) }
        }
    }

    private fun getGroup(id: Int): Group? {
        val response = client.getGroup(id) ?: return null

        if (!response.isSuccess)
            return null

        Log.d(LogName, "getGroup was successful -> found ${String(response.payload)}")
        return parseResponse(response, Group::class.java)
    }

    fun getGroups(onSuccess: (List<Group>) -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _getGroups(onSuccess, onError)
        }
    }

    private fun _getGroups(onSuccess: (List<Group>) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val groupIds = getGroupIds()
            val groups = groupIds.mapNotNull { getGroup(it) }

            launch(UI + handler) { onSuccess(groups) }
        }
    }

    fun turnGroupOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _turnGroupOn(id, onSuccess, onError)
        }
    }

    private fun _turnGroupOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.turnGroupOn(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess() }
        }
    }

    fun turnGroupOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _turnGroupOff(id, onSuccess, onError)
        }
    }

    private fun _turnGroupOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val response = client.turnGroupOff(id)
            if (response == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (!response.isSuccess) {
                launch(UI + handler) { onError() }
                return@launch
            }

            launch(UI + handler) { onSuccess() }
        }
    }

    fun toggleGroup(groupId: Int, onSuccess: () -> Unit, onError: () -> Unit) {
        queueService.addAction {
            return@addAction _toggleGroup(groupId, onSuccess, onError)
        }
    }

    private fun _toggleGroup(groupId: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val device = getGroup(groupId)
            if (device == null) {
                launch(UI + handler) { onError() }
                return@launch
            }

            if (device.on != null) {
                when (device.on) {
                    0 -> turnGroupOn(groupId, onSuccess, onError)
                    1 -> turnGroupOff(groupId, onSuccess, onError)
                    else -> turnGroupOn(groupId, onSuccess, onError)
                }
            } else {
                turnGroupOn(groupId, onSuccess, onError)
            }
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

    companion object {
        const val LogName = "TradfriService"

        private var instance: TradfriService? = null

        fun instance(context: Context): TradfriService {
            if (instance != null) return instance!!

            instance = TradfriService(context)
            return instance!!
        }
    }
}