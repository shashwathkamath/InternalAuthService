package org.example.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.example.controllers.InternalAuthController

/**
 * This end point is used by auth service to validated and check status on driver token
 * whether the driver is allowed to access station id or not.
 */
fun Route.internalAuthRoutes(){
    post("/verify") {
        InternalAuthController.handleMessage(call)
    }
}