package data.embedding

import com.ralphdugue.kogent.dataconnector.adapters.embedding.HuggingFaceEmbeddingModel
import common.BaseTest
import io.ktor.client.HttpClient

class HuggingFaceEmbeddingModelTest : BaseTest() {
    private lateinit var httpClient: HttpClient
    private lateinit var subject: HuggingFaceEmbeddingModel
}
