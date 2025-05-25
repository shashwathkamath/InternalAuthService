package org.example.models

@kotlinx.serialization.Serializable
data class AuthCallbackPayload(
    val stationId:String,
    val driverToken:String,
    val status: AccessStatus,
    val callbackUrl:String
)
