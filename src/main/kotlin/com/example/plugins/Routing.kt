package com.example.plugins

import com.google.gson.Gson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database


object DbSettings {
    val conn by lazy {
        Database.connect("jdbc:sqlite:db.db", driver = "org.sqlite.JDBC")
    }
}

fun Application.configureRouting() {
    val db = DB(DbSettings.conn)
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
        get("/messages") {
            val response = db.allMessages().toString()
            call.respond(HttpStatusCode.OK, response)
        }
        get("/message/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val response = db.getMessage(id)
            if (response != null) {
                call.respond(response.toString())
            }
            else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
        put("/message/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val body = call.receiveText()
            // if id doesn't exist will return 200 but not do anything
            if (body.isNotEmpty()) {
                db.updateMessage(id, body)
                call.respond(HttpStatusCode.OK)
            }
            else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}
