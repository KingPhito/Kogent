package query.engine

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.api.APIDataConnector
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.query.domain.entities.LLModel
import com.ralphdugue.kogent.query.domain.entities.QueryEngine
import com.ralphdugue.kogent.retrieval.domain.entities.Retriever
import common.BaseTest
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeEach

class KogentQueryEngineTest : BaseTest() {
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

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }
}