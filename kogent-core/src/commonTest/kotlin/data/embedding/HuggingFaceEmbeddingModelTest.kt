package data.embedding

import com.ralphdugue.kogent.data.adapters.embedding.HuggingFaceEmbeddingModel
import common.BaseTest
import io.ktor.client.HttpClient
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll

class HuggingFaceEmbeddingModelTest : BaseTest() {
    private lateinit var httpClient: HttpClient
    private lateinit var subject: HuggingFaceEmbeddingModel

    //@BeforeAll
}
