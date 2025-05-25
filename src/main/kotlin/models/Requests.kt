package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val stationId:String,
    val driverToken:String,
    val callbackUrl: String
)
