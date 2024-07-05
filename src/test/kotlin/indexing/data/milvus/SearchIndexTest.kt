package indexing.data.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.response.SearchResp
import io.milvus.v2.service.vector.response.SearchResp.SearchResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import utils.FakeDocumentFactory
import utils.RandomPrimitivesFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SearchIndexTest : BaseTest() {
    @MockK
    private lateinit var clientV2: MilvusClientV2
    private lateinit var subject: MilvusIndex

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(mainCoroutineDispatcher)
        subject =
            MilvusIndex(
                config =
                    IndexConfig.VectorDatabaseConfig(
                        connectionString = "localhost:19530",
                        vectorDatabaseType = VectorDatabaseOptions.MILVUS,
                    ),
                client = clientV2,
            )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        mainCoroutineDispatcher.close()
    }

    @Test
    fun `searchIndex should return a list of documents when query is successful`() =
        runTest {
            val query = RandomPrimitivesFactory.genRandomFloatArray()
            val expected = FakeDocumentFactory.genRandomDocumentList()
            val searchResults =
                expected.map {
                    SearchResult
                        .builder()
                        .id(it.id)
                        .entity(
                            mapOf(
                                "sourceName" to it.sourceName,
                                "sourceType" to it.sourceType,
                                "dialect" to it.dialect,
                                "embedding" to it.embedding.toList(),
                            ),
                        ).build()
                }
            val mockResponse =
                SearchResp
                    .builder()
                    .searchResults(listOf(searchResults))
                    .build()
            every { clientV2.search(any()) } returns mockResponse
            val actual = subject.searchIndex(sourceName = expected[0].sourceName, query = query, topK = 10)
            actual.forEachIndexed { index, document ->
                document as Document.SQLDocument
                assert(document.id == expected[index].id)
                assert(document.sourceName == expected[index].sourceName)
                assert(document.sourceType == expected[index].sourceType)
                assert(document.dialect == expected[index].dialect)
                assert(document.embedding.contentEquals(expected[index].embedding))
            }
        }

    @Test
    fun `searchIndex should return an empty list when query is unsuccessful`() =
        runTest {
            val query = RandomPrimitivesFactory.genRandomFloatArray()
            val expected = FakeDocumentFactory.genRandomDocumentList()
            every { clientV2.search(any()) } throws Exception("Failed to search")
            val actual = subject.searchIndex(sourceName = expected[0].sourceName, query = query, topK = 10)
            assert(actual.isEmpty())
        }
}
