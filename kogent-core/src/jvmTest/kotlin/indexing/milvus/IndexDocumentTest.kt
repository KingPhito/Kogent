package indexing.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.response.InsertResp
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import utils.FakeDocumentFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IndexDocumentTest : BaseTest() {
    @MockK
    private lateinit var clientV2: MilvusClientV2
    private lateinit var subject: MilvusIndex

    @BeforeTest
    override fun setUp() {
        super.setUp()
        subject =
            MilvusIndex(
                config =
                    VectorStoreConfig(
                        connectionString = "localhost:19530",
                        vectorDatabaseType = VectorStoreOptions.MILVUS,
                    ),
                client = clientV2,
            )
    }

    @AfterTest
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `indexDocument should return true when document is indexed successfully`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            val mockResponse =
                InsertResp
                    .builder()
                    .InsertCnt(1)
                    .build()
            every { clientV2.hasCollection(any()) } returns true
            every { clientV2.loadCollection(any()) } returns Unit
            every { clientV2.insert(any()) } returns mockResponse
            val expected = true
            val actual = subject.indexDocument(document)

            verify(exactly = 1) { clientV2.insert(any()) }
            assertEquals(expected, actual)
        }

    @Test
    fun `indexDocument should return false when document is not indexed successfully`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            every { clientV2.hasCollection(any()) } returns true
            every { clientV2.loadCollection(any()) } returns Unit
            every { clientV2.insert(any()) } throws Exception()
            val expected = false
            val actual = subject.indexDocument(document)

            verify(exactly = 1) { clientV2.insert(any()) }
            assertEquals(expected, actual)
        }

    @Test
    fun `indexDocument should create collection when collection does not exist and insert the document`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            val mockResponse =
                InsertResp
                    .builder()
                    .InsertCnt(1)
                    .build()
            every { clientV2.hasCollection(any()) } returns false
            every { clientV2.createCollection(any()) } returns Unit
            every { clientV2.getLoadState(any()) } returns true
            every { clientV2.insert(any()) } returns mockResponse
            val expected = true
            val actual = subject.indexDocument(document)

            verify(exactly = 1) { clientV2.createCollection(any()) }
            assertEquals(expected, actual)
        }

    @Test
    fun `indexDocument should return false when collection creation fails`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            every { clientV2.hasCollection(any()) } returns false
            every { clientV2.createCollection(any()) } throws Exception()
            val expected = false
            val actual = subject.indexDocument(document)

            verify(exactly = 1) { clientV2.createCollection(any()) }
            assertEquals(expected, actual)
        }
}
