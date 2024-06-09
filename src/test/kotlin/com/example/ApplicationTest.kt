package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Assertions.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testSuspend {
        val response = testApp.client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testEndpointTest1() = testSuspend {
        val response = testApp.client.get("/test1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("html", response.contentType()?.contentSubtype)
        assertContains(response.bodyAsText(), "Hello From Ktor")
    }

    companion object {
        lateinit var testApp: TestApplication

        @JvmStatic
        @BeforeAll
        fun setup()  {
            testApp = TestApplication {  }
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            testApp.stop()
        }
    }
}