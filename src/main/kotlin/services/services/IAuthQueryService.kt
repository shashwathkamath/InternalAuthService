package org.kamath.services

import org.example.models.AuthCallbackPayload

interface IAuthQueryService {
    suspend fun sendToQueue(authCallbackPayload: AuthCallbackPayload)
    suspend fun startConsumer():String?
}