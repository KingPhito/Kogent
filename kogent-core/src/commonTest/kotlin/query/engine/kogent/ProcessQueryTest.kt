package query.engine.kogent

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.query.adapters.KogentQueryEngine
import com.ralphdugue.kogent.query.domain.entities.LLMResponse
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.Operation
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import common.BaseTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDataSourceFactory
import utils.RandomPrimitivesFactory
import kotlin.test.assertEquals

class ProcessQueryTest : BaseTest() {
    private lateinit var subject: QueryEngine

    @MockK
    private lateinit var retriever: Retriever

    @MockK
    private lateinit var llModel: LLModel

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

    @MockK
    private lateinit var apiDataConnector: APIDataConnector

    @MockK
    private lateinit var sqlDataConnector: SQLDataConnector

    private val json = Json { prettyPrint = true }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        subject = KogentQueryEngine(
            retriever = retriever,
            llModel = llModel,
            dataSourceRegistry = dataSourceRegistry,
            apiDataConnector = apiDataConnector,
            sqlDataConnector = sqlDataConnector
        )
    }

    @Test
    fun `processQuery should return an appropriate error message when the retriever fails`() =
        runTest {
            val errorMessage = RandomPrimitivesFactory.genRandomString()
            coEvery { retriever.retrieve(any()) } returns Result.failure(Exception(errorMessage))
            val expected = "There was an error processing the request: $errorMessage"
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return an appropriate error message when the llModel fails`() =
        runTest {
            val errorMessage = RandomPrimitivesFactory.genRandomString()
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns errorMessage
            val expected = "Received an invalid response from the model: $errorMessage. \nPlease try again."
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return an appropriate error message when the data source registry fails`() =
        runTest {
            val errorMessage = RandomPrimitivesFactory.genRandomString()
            val operation = Operation.SqlQuery(
                dataSourceId = RandomPrimitivesFactory.genRandomString(),
                query = RandomPrimitivesFactory.genRandomString()
            )
            val needsUpdate = LLMResponse(needsUpdate = true, operation = operation, answer = "")
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns json.encodeToString(needsUpdate)
            coEvery { dataSourceRegistry.getDataSourceById(any()) } returns Result.failure(Exception(errorMessage))
            val expected = "There was an error finding the data source: $errorMessage"
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return an appropriate error message when the apiDataConnector fails`() =
        runTest {
            val errorMessage = RandomPrimitivesFactory.genRandomString()
            val operation = Operation.ApiCall(
                dataSourceId = RandomPrimitivesFactory.genRandomString(),
                body = RandomPrimitivesFactory.genRandomString(),
                endpoint = RandomPrimitivesFactory.genRandomString()
            )
            val apiDataSource = FakeDataSourceFactory.createAPIDatasource()
            val needsUpdate = LLMResponse(needsUpdate = true, operation = operation, answer = "")
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns json.encodeToString(needsUpdate)
            coEvery { dataSourceRegistry.getDataSourceById(any()) } returns Result.success(apiDataSource)
            coEvery { apiDataConnector.postData(any(), any()) } returns Result.failure(Exception(errorMessage))
            val expected = "There was an error processing the request: $errorMessage"
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return an appropriate error message when the sqlDataConnector fails`() =
        runTest {
            val errorMessage = RandomPrimitivesFactory.genRandomString()
            val operation = Operation.SqlQuery(
                dataSourceId = RandomPrimitivesFactory.genRandomString(),
                query = RandomPrimitivesFactory.genRandomString()
            )
            val sqlDataSource = FakeDataSourceFactory.createSQLDatasource()
            val needsUpdate = LLMResponse(needsUpdate = true, operation = operation, answer = "")
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns json.encodeToString(needsUpdate)
            coEvery { dataSourceRegistry.getDataSourceById(any()) } returns Result.success(sqlDataSource)
            coEvery { sqlDataConnector.writeQuery(any(), any()) } returns Result.failure(Exception(errorMessage))
            val expected = "There was an error processing the request: $errorMessage"
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return an appropriate success message when the data source is updated successfully`() =
        runTest {
            val operation = Operation.SqlQuery(
                dataSourceId = RandomPrimitivesFactory.genRandomString(),
                query = RandomPrimitivesFactory.genRandomString()
            )
            val sqlDataSource = FakeDataSourceFactory.createSQLDatasource()
            val needsUpdate = LLMResponse(needsUpdate = true, operation = operation, answer = "")
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns json.encodeToString(needsUpdate)
            coEvery { dataSourceRegistry.getDataSourceById(any()) } returns Result.success(sqlDataSource)
            coEvery { sqlDataConnector.writeQuery(any(), any()) } returns Result.success(Unit)
            val expected = needsUpdate.answer + "\nData source updated successfully."
            val actual = subject.processQuery("query")
            assertEquals(expected, actual)
        }

    @Test
    fun `processQuery should return the appropriate answer when the llModel does not need an update`() =
        runTest {
            val answer = RandomPrimitivesFactory.genRandomString()
            val needsUpdate = LLMResponse(needsUpdate = false, operation = null, answer = answer)
            coEvery { retriever.retrieve(any()) } returns Result.success(emptyList())
            coEvery { llModel.query(any(), any()) } returns json.encodeToString(needsUpdate)
            val actual = subject.processQuery("query")
            assertEquals(answer, actual)
        }
}