package thekolo.de.quicktilesforikeatradfri.utils

import thekolo.de.quicktilesforikeatradfri.Device

object DeviceUtil {
    fun isDeviceOn(device: Device?): Boolean {
        if (device == null) return false
        if (device.states == null) return false
        if (device.states.isEmpty()) return false
        if (device.states.first().on == null) return false
        return device.states.first().on!! != 0
    }
}