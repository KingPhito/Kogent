package retrieval

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.retrieval.adapters.KeywordRetriever
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import common.BaseTest
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.FakeDataSourceFactory
import utils.FakeDocumentFactory
import utils.RandomPrimitivesFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeywordRetrieverTest : BaseTest() {
    private lateinit var subject: Retriever

    @MockK
    private lateinit var index: Index

    @MockK
    private lateinit var embeddingModel: EmbeddingModel

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

    private val dataSources: List<List<SQLDataSource>> = List(3) {
        FakeDataSourceFactory.createSQLDatasourceCollection()
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        coEvery { dataSourceRegistry.getDataSources() } returns Result.success(dataSources.flatten())
        coEvery {
            embeddingModel.getEmbedding(any())
        } returns Result.success(RandomPrimitivesFactory.genRandomFloatList())
        subject = KeywordRetriever(index, embeddingModel, dataSourceRegistry)
    }

    @Test
    fun `retrieve should return the correct list of documents based on the query`() =
        runTest {
            val collections = mutableListOf<Document>()
            dataSources.forEach { sources ->
                val collection = FakeDocumentFactory.createDocumentCollection(sources)
                collections.addAll(collection)
                coEvery { index.searchIndex(sources.first().databaseName, any()) } returns collection
            }
            val query = "query " + dataSources.joinToString(" ") { it.first().identifier }
            val documents = subject.retrieve(query)
            assertTrue(documents.isSuccess, "Expected Result.success but was ${documents.exceptionOrNull()}")
            val expected = collections.sortedBy { it.id }
            val actual = documents.getOrNull()!!.sortedBy { it.id }
            assertEquals(expected.size, actual.size)
            for (i in expected.indices) {
                assertEquals(expected[i].id, actual[i].id)
                assertEquals(expected[i].sourceName, actual[i].sourceName)
                assertEquals(expected[i].sourceType, actual[i].sourceType)
                assertEquals(expected[i].text, actual[i].text)
                assertEquals(expected[i].embedding.toList(), actual[i].embedding.toList())
            }

        }

    @Test
    fun `retrieve should return an empty list when the query does not match any data source`() =
        runTest {
            val query = "query " + RandomPrimitivesFactory.genRandomString()
            val documents = subject.retrieve(query)
            assertTrue(documents.isSuccess, "Expected Result.success but was ${documents.exceptionOrNull()}")
            assertTrue(documents.getOrNull()!!.isEmpty())
        }
}