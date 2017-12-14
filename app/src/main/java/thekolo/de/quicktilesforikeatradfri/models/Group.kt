package thekolo.de.quicktilesforikeatradfri.models

import com.google.gson.annotations.SerializedName

class Group(@SerializedName("9003") override val id: Int,
            @SerializedName("9001") override val name: String,
            @SerializedName("9018") val groupDevicesFirstLevel: GroupDevicesFirstLevel,
            @SerializedName("5850") val on: Int? = null,
            @SerializedName("5851") val brightness: Int? = null) : TradfriDevice(id, name)

class GroupDevicesFirstLevel(@SerializedName("15002") val firstLevel: GroupDevices)

class GroupDevices(@SerializedName("9003") val deviceIds: List<Int>)

class GroupUpdater(@SerializedName("5850") val on: Int)