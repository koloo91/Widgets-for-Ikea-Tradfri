package thekolo.de.quicktilesforikeatradfri

import com.google.gson.annotations.SerializedName
import thekolo.de.quicktilesforikeatradfri.models.TradfriDevice

class Device(@SerializedName("9003") override val id: Int,
             @SerializedName("9001") override val name: String,
             @SerializedName("3") val type: DeviceType,
             @SerializedName("3311") val states: List<DeviceState>?) : TradfriDevice(id, name)

class DeviceState(@SerializedName("5850") var on: Int? = null,
                  @SerializedName("5706") val color: String? = null,
                  @SerializedName("5851") val brightness: Int? = null)

class DeviceType(@SerializedName("1") val name: String,
                 @SerializedName("3") val version: String,
                 @SerializedName("9") val battery: Float?)

class DeviceUpdater(@SerializedName("3311") val states: List<DeviceState>)