package org.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.routing
import io.ktor.utils.io.printStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.example.routes.internalAuthRoutes
import org.kamath.config.DatabaseConfig
import org.kamath.db.seed.SeedHelper
import org.kamath.services.AuthQueryService
import org.kamath.services.IAuthQueryService
import org.kamath.utils.Constants
import org.kamath.utils.logger

fun createApp(): Application.() -> Unit = {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    DatabaseConfig.init()
    SeedHelper.seedDummyUsers()
    routing {
        internalAuthRoutes()
    }
    environment.monitor.subscribe(ApplicationStarted){
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch {
            try {
                val authService: IAuthQueryService = AuthQueryService
                authService.startConsumer()
            }
            catch (e:Exception){
                logger.info(Constants.CONSUMER_FAILED,e)
                e.printStack()
            }
        }
    }
}