package data.api

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients

class AuthApi {

    fun validateProjectIdAndToken(projectId: String, token: String): Boolean {
        val client = HttpClients.createDefault()
        val get = HttpGet("http://127.0.0.1:8000/validate?projectid=${projectId}&token=${token}")
        val response = client.execute(get)
        return when (response.statusLine.statusCode) {
            200 -> true
            else -> false
        }
    }
}