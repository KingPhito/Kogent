package utils

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.kogent.cache.DataSourceRegistryDB

expect class TestDriverFactory() {
    fun createTestDriver(): SqlDriver
}

object FakeRegistryFactory {
    fun createFakeLocalRegistry(): DataSourceRegistryDB {

        return DataSourceRegistryDB(TestDriverFactory().createTestDriver())
    }
}