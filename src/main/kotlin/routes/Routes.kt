package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.controllers.InternalAuthController


fun Route.internalAuthRoutes(){
    post("/verify") {
        InternalAuthController.handleMessage(call)
    }
}