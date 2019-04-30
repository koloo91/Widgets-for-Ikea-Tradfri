package thekolo.de.quicktilesforikeatradfri.utils

import thekolo.de.quicktilesforikeatradfri.models.BulbState
import thekolo.de.quicktilesforikeatradfri.models.Device
import thekolo.de.quicktilesforikeatradfri.models.Group

object DeviceUtil {

    fun isDeviceOn(device: Device?): Boolean {
        if (device == null) return false
        if (device.states == null) return false
        if (device.states.isEmpty()) return false
        if (device.states.first().on == null) return false
        return device.states.first().on!! != 0
    }

    fun isGroupOn(group: Group?): Boolean {
        if (group == null) return false
        if (group.on == null) return false
        return group.on!! == BulbState.On
    }
}