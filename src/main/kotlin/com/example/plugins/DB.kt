package com.example.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sun.org.apache.xml.internal.serializer.utils.Utils.messages
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
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

    suspend fun allMessages(): List<String?> {
        // doing it like this so characters like ' aren't escaped
        val builder = GsonBuilder()
        builder.disableHtmlEscaping()
        val gson = builder.create()
//        val gson = Gson()
        // ends up with an array of json objects
        return dbQuery {
            message.selectAll().orderBy(message.id).map {
               gson.toJson(singleMessage(it[message.id], it[message.message]))
            }
        }
    }

    suspend fun getMessage(id: Int): String? {
        val builder = GsonBuilder()
        builder.disableHtmlEscaping()
        val gson = builder.create()
        return dbQuery {
            message.selectAll().where { message.id eq id }
                .map {gson.toJson(singleMessage(it[message.id], it[message.message]))}
                .singleOrNull()
        }
    }

    suspend fun updateMessage(id: Int, messageText: String) {
        dbQuery {
            message.update({ message.id eq id }) {
                it[message] = messageText
            }
        }
    }
}