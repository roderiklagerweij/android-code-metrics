package data.api

import com.google.gson.Gson
import domain.AnalysisResult
import org.gradle.internal.impldep.org.apache.http.client.methods.HttpPost
import org.gradle.internal.impldep.org.apache.http.entity.ContentType
import org.gradle.internal.impldep.org.apache.http.entity.StringEntity
import org.gradle.internal.impldep.org.apache.http.impl.client.HttpClients

class UploadApi {

    fun postAnalysisResult(analysisResult: AnalysisResult) {
        val client = HttpClients.createDefault()
        val post = HttpPost("http://127.0.0.1:8000/results")
        post.entity = StringEntity(
            Gson().getAdapter(AnalysisResult::class.java).toJson(analysisResult),
            ContentType.APPLICATION_JSON
        )

        val response = client.execute(post)
        if (response.statusLine.statusCode == 200) {
            println("Results posted")
        } else {
            println("Error posting results")
        }
    }
}