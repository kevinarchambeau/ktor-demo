package com.example.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

data class singleMessage(val id: Int, val message: String)

class DB(private val db: Database) {
    object message : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val message: Column<String> = text("message")
    }

    init {
        transaction(db) {}
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun allMessages():List<String?> {
        // doing it like this so characters like ' aren't escaped
        val builder = GsonBuilder()
        builder.disableHtmlEscaping()
        val gson = builder.create()
//        val gson = Gson()
        // ends up with an array of json objects
        return dbQuery {
            message.selectAll().orderBy(message.id).map {
                gson.toJson(singleMessage(it[message.id], it[message.message])) }
        }
    }
}