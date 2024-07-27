package query.models

import com.ralphdugue.kogent.query.adapters.HuggingFaceLLModel
import com.ralphdugue.kogent.query.domain.entities.HuggingFaceLLModelConfig
import com.ralphdugue.kogent.query.domain.entities.LLModel
import common.BaseTest
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import utils.MockHttpClientFactory
import utils.RandomPrimitivesFactory
import kotlin.test.assertEquals

class HuggingFaceLLModelTest : BaseTest() {
    private lateinit var subject: LLModel
    private lateinit var httpClient: HttpClient

    @Test
    fun `query should return the expected json body as a string`() =
        runTest {
            val expected = RandomPrimitivesFactory.genRandomString()
            httpClient = MockHttpClientFactory.createClient(
                json = expected,
                status = HttpStatusCode.OK
            )
            subject = HuggingFaceLLModel(
                config = HuggingFaceLLModelConfig(
                    endpoint = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.query(RandomPrimitivesFactory.genRandomString())
            assertEquals(expected, actual)
        }

    @Test
    fun `query should return an error message when the request fails`() =
        runTest {
            val errorMessage = "Failed to fetch data from source"
            httpClient = MockHttpClientFactory.createClient(
                json = "",
                status = HttpStatusCode.BadRequest
            )
            subject = HuggingFaceLLModel(
                config = HuggingFaceLLModelConfig(
                    endpoint = RandomPrimitivesFactory.genRandomString(),
                    apiToken = RandomPrimitivesFactory.genRandomString()
                ),
                client = httpClient
            )
            val actual = subject.query(RandomPrimitivesFactory.genRandomString())
            val expected = "There was an error generating the response: $errorMessage"
            assertEquals(expected, actual)
        }
}