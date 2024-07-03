package indexing.data.milvus

import com.ralphdugue.kogent.indexing.adapters.MilvusIndex
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorDatabaseOptions
import common.BaseTest
import io.milvus.v2.client.MilvusClientV2
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class DeleteDocumentTest : BaseTest() {
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
    fun `deleteDocument should return true when document is deleted successfully`() {
    }
}
