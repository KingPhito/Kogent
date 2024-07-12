package data.embedding

import com.ralphdugue.kogent.data.adapters.embedding.HuggingFaceEmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingConfig
import common.BaseTest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.FakeHttpClientFactory
import utils.RandomPrimitivesFactory

class HuggingFaceEmbeddingModelTest : BaseTest() {
    private lateinit var httpClient: HttpClient
    private lateinit var subject: HuggingFaceEmbeddingModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(mainCoroutineDispatcher)

    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getEmbedding should return a list of floats when successful`()  =
        runTest {
            val expected = RandomPrimitivesFactory.genRandomFloatList()
            httpClient = FakeHttpClientFactory.createClient(
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
    fun `getEmbedding should throw an exception when unsuccessful`()  =
        runTest {
            httpClient = FakeHttpClientFactory.createClient(
                json = "",
                status = HttpStatusCode.InternalServerError
            )
            subject = HuggingFaceEmbeddingModel(
                config = EmbeddingConfig.HuggingFaceEmbeddingConfig(
                    model = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            assertThrows<Exception> {
                subject.getEmbedding(RandomPrimitivesFactory.genRandomString())
            }
        }
}
