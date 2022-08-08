package data.api

import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients

//import org.gradle.internal.impldep.org.apache.http.client.methods.HttpGet
//import org.gradle.internal.impldep.org.apache.http.impl.client.HttpClients

class AuthApi {

    fun validateProjectIdAndToken(projectId: String, token: String): Boolean {
        val client = HttpClients.createDefault()
        val get = HttpGet("http://127.0.0.1:8000/validate?projectid=${projectId}&token=${token}")
        val response = client.execute(get)

        return when (response.code) {
            200 -> true
            else -> false
        }
    }
}