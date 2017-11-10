package thekolo.de.quicktilesforikeatradfri.models

import com.google.gson.annotations.SerializedName

class RegisterResult(@SerializedName("9091") val preSharedKey: String,
                     @SerializedName("9029") val gatewayVersion: String)