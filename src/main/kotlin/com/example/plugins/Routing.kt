package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        staticResources("/content", "content")
        get("/") {
            call.respondText("Hello World!")
        }
        route("/error-test") {
            get {
                throw IllegalStateException("Too Busy")
            }
        }
        get("/test1") {
            val text = "<h1>Hello From Ktor!</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
    }
}
