package thekolo.de.quicktilesforikeatradfri.models

import com.google.gson.annotations.SerializedName

class Device(@SerializedName("9003") override val id: Int,
             @SerializedName("9001") override val name: String,
             @SerializedName("3") val type: DeviceType?,
             @SerializedName("3311") val states: List<DeviceState>?) : TradfriDevice(id, name)