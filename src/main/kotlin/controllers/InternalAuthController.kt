package org.example.controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.models.AccessStatus
import org.example.models.AuthCallbackPayload
import org.example.models.AuthRequest
import org.example.models.AuthResponse
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kamath.db.tables.AccessLogs
import org.kamath.db.tables.AccessRules
import org.kamath.services.AuthQueryService
import org.kamath.services.IAuthQueryService
import org.kamath.utils.logger
import java.time.LocalDateTime

/**
 * Once we get data from messaging queue, we call the API to access db
 * That api will be mapped to below controller for match in db
 * The decision is then persisted to AccessLogs DB
 */
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
                persistData(response)
                val authCallbackPayload = AuthCallbackPayload(
                    stationId = request.stationId,
                    driverToken = request.driverToken,
                    status = status,
                    callbackUrl = request.callbackUrl
                )
                val authService: IAuthQueryService = AuthQueryService
                authService.sendToQueue(authCallbackPayload)
                call.respond(response)
            }catch (e: Exception){
                logger.error("Issue while checking db $e")
                call.respond(HttpStatusCode.InternalServerError,"Verification failed")
            }
    }

    private fun persistData(response: AuthResponse){
            try{
                val insertResult = transaction {
                    AccessLogs.insert {
                        it[stationId] = response.stationId
                        it[driverToken] = response.driverToken
                        it[status] = response.status.name
                        it[timestamp] = LocalDateTime.now()
                    }
                }
                logger.info("Status data is persisted with result ${insertResult.insertedCount}")
            }
            catch (e: Exception){
                logger.error("Issue while persisting data $e")
            }
        }
}