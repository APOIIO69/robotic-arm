package com.apollo.roboarm.data.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun createKtorClient() = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    defaultRequest {
        // Emulator:       http://10.0.2.2:8000
        // Physical device: http://<VM-IP>:8000  (find with `ip addr` in Ubuntu)
        url("http://192.168.0.100:8000")
    }
}
