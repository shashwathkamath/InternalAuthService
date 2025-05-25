package org.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.kamath.config.DatabaseConfig
import org.kamath.db.seed.SeedHelper

fun createApp(): Application.() -> Unit = {
    install(ContentNegotiation) {
        json(Json { prettyPrint = true })
    }
    DatabaseConfig.init()
    SeedHelper.seedDummyUsers()

}