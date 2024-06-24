package dataconnector.data

import dataconnector.domain.entities.KogentSQLDataConnector
import kotlin.test.BeforeTest

class KogentSQLKogentDataConnectorlTest {
    private lateinit var subject: KogentSQLDataConnector

    @BeforeTest
    fun setUp() {
        subject = SQLDataConnectorImpl()
    }
}
