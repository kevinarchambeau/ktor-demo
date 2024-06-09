package com.example.plugins

import org.jetbrains.exposed.sql.Database

class DB(private val db: Database) {
    fun test(){
        println("db is: $db")
    }
}