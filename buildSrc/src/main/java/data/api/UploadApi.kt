package data.api

import domain.AnalysisResult
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients

class UploadApi {

    fun postAnalysisResult(analysisResult: AnalysisResult) {
        val client = HttpClients.createDefault()
        val post = HttpPost("http://127.0.0.1:8000/results")
        val response = client.execute(post)
        if (response.statusLine.statusCode == 200) {
            println("Results posted")
        } else {
            println("Error posting results")
        }
    }

}