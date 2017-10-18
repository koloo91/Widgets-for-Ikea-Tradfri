package thekolo.de.widgetsforikeatradfri.utils

import thekolo.de.widgetsforikeatradfri.Device

object DeviceUtil {
    fun isDeviceOn(device: Device?): Boolean {
        if (device == null) return false
        if (device.states == null) return false
        if (device.states.isEmpty()) return false
        if (device.states.first().on == null) return false
        return device.states.first().on!! != 0
    }
}