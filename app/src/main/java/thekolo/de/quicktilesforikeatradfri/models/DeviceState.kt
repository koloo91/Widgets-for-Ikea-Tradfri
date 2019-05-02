package thekolo.de.quicktilesforikeatradfri.models

import com.google.gson.annotations.SerializedName

class DeviceState(@SerializedName("5850") var on: Int? = null,
                  @SerializedName("5706") val color: String? = null,
                  @SerializedName("5851") val brightness: Int? = null)