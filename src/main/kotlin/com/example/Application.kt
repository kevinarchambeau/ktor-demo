package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText { "App in illegal state as: ${cause.message}" }
        }
    }
    configureRouting()
}
