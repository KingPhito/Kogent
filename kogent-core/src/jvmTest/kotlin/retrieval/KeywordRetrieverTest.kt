package retrieval

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.retrieval.adapters.KeywordRetriever
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import common.BaseTest
import io.mockk.impl.annotations.MockK
import kotlin.test.BeforeTest

class KeywordRetrieverTest : BaseTest() {
    private lateinit var subject: Retriever

    @MockK
    private lateinit var index: Index

    @MockK
    private lateinit var embeddingModel: EmbeddingModel

    @MockK
    private lateinit var dataSourceRegistry: DataSourceRegistry

    @BeforeTest
    override fun setUp() {
        super.setUp()
        subject = KeywordRetriever(index, embeddingModel, dataSourceRegistry)
    }
}