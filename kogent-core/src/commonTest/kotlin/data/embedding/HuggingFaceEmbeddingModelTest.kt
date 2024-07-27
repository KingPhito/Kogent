package data.embedding

import com.ralphdugue.kogent.data.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.HuggingFaceEmbeddingConfig
import common.BaseTest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import utils.MockHttpClientFactory
import utils.RandomPrimitivesFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HuggingFaceEmbeddingModelTest : BaseTest() {
    private lateinit var httpClient: HttpClient
    private lateinit var subject: EmbeddingModel

    @Test
    fun `getEmbedding should return a list of floats when successful`()  =
        runTest {
            val expected = RandomPrimitivesFactory.genRandomFloatList()
            httpClient = MockHttpClientFactory.createClient(
                json = "$expected",
                status = HttpStatusCode.OK
            )
            subject = HuggingFaceEmbeddingModel(
                config = HuggingFaceEmbeddingConfig(
                    endpoint = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.getEmbedding(RandomPrimitivesFactory.genRandomString())
            assertTrue(actual.isSuccess, "Expected success but got ${actual.exceptionOrNull()}")
            assertEquals(actual.getOrNull(), expected, "Expected $expected but got $actual")
        }

    @Test
    fun `getEmbedding should return a failure when unsuccessful`() =
        runTest {
            httpClient = MockHttpClientFactory.createClient(
                json = "",
                status = HttpStatusCode.BadRequest
            )
            subject = HuggingFaceEmbeddingModel(
                config = HuggingFaceEmbeddingConfig(
                    endpoint = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.getEmbedding(RandomPrimitivesFactory.genRandomString())
            assert(actual.isFailure)
        }
}
