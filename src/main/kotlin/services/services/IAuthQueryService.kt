package org.kamath.services

import org.example.models.AuthRequest

interface IAuthQueryService {
    suspend fun sendToQueue(authRequest: AuthRequest)
    suspend fun startConsumer():String?
}