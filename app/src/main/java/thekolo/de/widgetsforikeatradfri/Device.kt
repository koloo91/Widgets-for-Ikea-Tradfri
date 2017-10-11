package thekolo.de.widgetsforikeatradfri

import com.google.gson.annotations.SerializedName

class Device(@SerializedName("9003") val id: Int,
             @SerializedName("9001") val name: String,
             @SerializedName("3") val type: DeviceType,
             @SerializedName("3311") val states: List<DeviceState>)

class DeviceState(@SerializedName("5850") val on: Int? = null,
                  @SerializedName("5706") val color: String? = null,
                  @SerializedName("5851") val brightness: Int? = null)

class DeviceType(@SerializedName("1") val name: String,
                 @SerializedName("3") val version: String)

class DeviceUpdater(@SerializedName("3311") val states: List<DeviceState>)