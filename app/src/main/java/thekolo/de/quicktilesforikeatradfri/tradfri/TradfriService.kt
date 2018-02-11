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
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil

class TradfriService(context: Context) {
    private lateinit var client: TradfriClient
    private val gson = Gson()

    private val handler = CoroutineExceptionHandler { _, ex ->
        Log.println(Log.ERROR, "TradfriService", Log.getStackTraceString(ex))
    }

    init {
        refreshClient(context)
    }

    fun refreshClient(context: Context) {
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

    fun register(identity: String, onSuccess: (RegisterResult) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return register(identity, onSuccess, {
            if (retryCounter > 0) register(identity, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun register(identity: String, onSuccess: (RegisterResult) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
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

            launch(UI + handler) {
                client.identity = identity
                client.preSharedKey = result.preSharedKey
                client.reload()

                onSuccess(result)
            }
        }
    }

    fun ping(onSuccess: (String) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return ping(onSuccess, {
            if (retryCounter > 0) ping(onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun ping(onSuccess: (String) -> Unit, onError: () -> Unit): Job {
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

    fun toggleAllOn(onFinish: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val ids = getDeviceIds()

            val allJobs = ids.map { turnDeviceOn(it, {}, {}) }

            allJobs.forEach { it.join() }

            launch(UI + handler) {
                onFinish()
            }
        }
    }

    fun toggleAllOff(onFinish: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val ids = getDeviceIds()

            val allJobs = ids.map { turnDeviceOff(it, {}, {}) }

            allJobs.forEach { it.join() }

            launch(UI + handler) {
                onFinish()
            }
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

    fun getDevice(id: Int, onSuccess: (Device) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return getDevice(id, onSuccess, {
            if (retryCounter > 0) getDevice(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun getDevice(id: Int, onSuccess: (Device) -> Unit, onError: () -> Unit): Job {
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


    fun getDevices(onSuccess: (List<Device>) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return getDevices(onSuccess, {
            if (retryCounter > 0) getDevices(onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun getDevices(onSuccess: (List<Device>) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val deviceIds = getDeviceIds()

            val devices = deviceIds.mapNotNull { id ->
                getDevice(id)
            }.sortedBy { it.name }

            launch(UI + handler) { onSuccess(devices) }
        }
    }

    fun getDevices(): List<Device> {
        val deviceIds = getDeviceIds()

        return deviceIds.mapNotNull { id ->
            getDevice(id)
        }.filter { device ->
                    !device.type.name.contains("remote control")
                }.sortedBy { it.name }
    }

    fun turnDeviceOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return turnDeviceOn(id, onSuccess, {
            if (retryCounter > 0) turnDeviceOn(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun turnDeviceOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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

    fun turnDeviceOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return turnDeviceOff(id, onSuccess, {
            if (retryCounter > 0) turnDeviceOff(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun turnDeviceOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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

    fun toggleDevice(deviceId: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return toggleDevice(deviceId, onSuccess, {
            if (retryCounter > 0) toggleDevice(deviceId, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun toggleDevice(deviceId: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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

    fun getGroup(id: Int, onSuccess: (Group) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return getGroup(id, onSuccess, {
            if (retryCounter > 0) getGroup(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun getGroup(id: Int, onSuccess: (Group) -> Unit, onError: () -> Unit): Job {
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

    fun getGroups(onSuccess: (List<Group>) -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return getGroups(onSuccess, {
            if (retryCounter > 0) getGroups(onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun getGroups(onSuccess: (List<Group>) -> Unit, onError: () -> Unit): Job {
        return launch(CommonPool + handler) {
            val groupIds = getGroupIds()
            val groups = groupIds.mapNotNull { getGroup(it) }.sortedBy { it.name }

            launch(UI + handler) { onSuccess(groups) }
        }
    }

    fun getGroups(): List<Group> {
        val groupIds = getGroupIds()
        return groupIds.mapNotNull { getGroup(it) }.sortedBy { it.name }
    }

    fun turnGroupOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return turnGroupOn(id, onSuccess, {
            if (retryCounter > 0) turnGroupOn(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun turnGroupOn(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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

    fun turnGroupOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return turnGroupOff(id, onSuccess, {
            if (retryCounter > 0) turnGroupOff(id, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun turnGroupOff(id: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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

    fun toggleGroup(groupId: Int, onSuccess: () -> Unit, onError: () -> Unit, retryCounter: Int = Retries): Job {
        return toggleGroup(groupId, onSuccess, {
            if (retryCounter > 0) toggleGroup(groupId, onSuccess, onError, retryCounter - 1)
            else onError()
        })
    }

    private fun toggleGroup(groupId: Int, onSuccess: () -> Unit, onError: () -> Unit): Job {
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
        const val Retries = 3
        const val LogName = "TradfriService"

        private var instance: TradfriService? = null

        fun instance(context: Context): TradfriService {
            if (instance != null) return instance!!

            instance = TradfriService(context)
            return instance!!
        }
    }
}