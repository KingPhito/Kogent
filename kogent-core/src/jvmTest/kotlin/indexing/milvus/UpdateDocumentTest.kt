package indexing.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.response.UpsertResp
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import utils.FakeDocumentFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateDocumentTest : BaseTest() {
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
    fun `updateDocument should return true when document is updated successfully`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            val mockResponse = UpsertResp.builder().upsertCnt(1).build()
            every { clientV2.upsert(any()) } returns mockResponse

            val result = subject.updateDocument(document)
            assertEquals(true, result)
            verify(exactly = 1) { clientV2.upsert(any()) }
        }

    @Test
    fun `updateDocument should return false when document is not updated successfully`() =
        runTest {
            val document = FakeDocumentFactory.createSQLDocument()
            every { clientV2.upsert(any()) } throws Exception()

            val result = subject.updateDocument(document)
            assertEquals(false, result)
            verify(exactly = 1) { clientV2.upsert(any()) }
        }
}
