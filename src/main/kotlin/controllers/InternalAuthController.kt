package org.example.controllers

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import org.example.models.AccessStatus
import org.example.models.AuthRequest
import org.example.models.AuthResponse
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kamath.db.tables.AccessRules
import org.kamath.utils.logger

object InternalAuthController {

    suspend fun handleMessage(call: ApplicationCall){
        try{
            val request = call.receive<AuthRequest>()
            val status = transaction {
                val match = AccessRules.select {
                    (AccessRules.stationId eq request.stationId) and
                            (AccessRules.driverToken eq request.driverToken)
                }.count() > 0
                if (match) AccessStatus.allowed else AccessStatus.not_allowed
            }
            val response = AuthResponse(
                stationId = request.stationId,
                driverToken = request.driverToken,
                status = status
            )
            logger.info("Returning access decision $response")
            call.respond(response)
        }catch (e: Exception){
            logger.error("Issue while checking db $e")
            call.respond(HttpStatusCode.InternalServerError,"Verification failed")
        }

    }
}