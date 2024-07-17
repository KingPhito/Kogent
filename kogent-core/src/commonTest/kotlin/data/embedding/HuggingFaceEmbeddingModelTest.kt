package data.embedding

import com.ralphdugue.kogent.data.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import common.BaseTest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import utils.MockHttpClientFactory
import utils.RandomPrimitivesFactory

class HuggingFaceEmbeddingModelTest : BaseTest() {
    private lateinit var httpClient: HttpClient
    private lateinit var subject: HuggingFaceEmbeddingModel

    @Test
    fun `getEmbedding should return a list of floats when successful`()  =
        runTest {
            val expected = RandomPrimitivesFactory.genRandomFloatList()
            httpClient = MockHttpClientFactory.createClient(
                json = "$expected",
                status = HttpStatusCode.OK
            )
            subject = HuggingFaceEmbeddingModel(
                config = EmbeddingConfig.HuggingFaceEmbeddingConfig(
                    model = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.getEmbedding(RandomPrimitivesFactory.genRandomString())
            assert(actual == expected)
        }

    @Test
    fun `getEmbedding should return an empty list when unsuccessful`() =
        runTest {
            httpClient = MockHttpClientFactory.createClient(
                json = "",
                status = HttpStatusCode.BadRequest
            )
            subject = HuggingFaceEmbeddingModel(
                config = EmbeddingConfig.HuggingFaceEmbeddingConfig(
                    model = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.getEmbedding(RandomPrimitivesFactory.genRandomString())
            assert(actual.isEmpty())
        }
}
