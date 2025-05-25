package org.example.models

import kotlinx.serialization.Serializable


@Serializable
enum class AccessStatus {
    allowed,
    not_allowed,
    unknown,
    invalid
}

@Serializable
data class AuthResponse(
    val stationId:String,
    val driverToken:String,
    val status: AccessStatus
)
