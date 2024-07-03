package indexing.data.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.Document
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.milvus.v2.service.vector.response.UpsertResp
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import utils.RandomsFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateDocumentTest : BaseTest() {
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
    fun `updateDocument should return true when document is updated successfully`() =
        runTest {
            val document =
                Document.SQLDocument(
                    id = RandomsFactory.genRandomString(),
                    sourceName = RandomsFactory.genRandomString(),
                    dialect = RandomsFactory.genRandomString(),
                    embedding = RandomsFactory.genRandomFloatList(),
                )
            val mockResponse = UpsertResp.builder().upsertCnt(1).build()
            every { clientV2.upsert(any()) } returns mockResponse

            val result = subject.updateDocument(document)
            assertEquals(true, result)
            verify(exactly = 1) { clientV2.upsert(any()) }
        }

    @Test
    fun `updateDocument should return false when document is not updated successfully`() =
        runTest {
            val document =
                Document.SQLDocument(
                    id = RandomsFactory.genRandomString(),
                    sourceName = RandomsFactory.genRandomString(),
                    dialect = RandomsFactory.genRandomString(),
                    embedding = RandomsFactory.genRandomFloatList(),
                )
            every { clientV2.upsert(any()) } throws Exception()

            val result = subject.updateDocument(document)
            assertEquals(false, result)
            verify(exactly = 1) { clientV2.upsert(any()) }
        }
}
