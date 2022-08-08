package data.api

import com.google.gson.Gson
import domain.AnalysisResult
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.io.entity.StringEntity

class UploadApi {

    fun postAnalysisResult(analysisResult: AnalysisResult) {
        val client = HttpClients.createDefault()
        val post = HttpPost("http://127.0.0.1:8000/results")
        post.entity = StringEntity(
            Gson().getAdapter(AnalysisResult::class.java).toJson(analysisResult),
            ContentType.APPLICATION_JSON
        )

        val response = client.execute(post)
        if (response.code == 200) {
            println("Results posted")
        } else {
            println("Error posting results")
        }
    }
}