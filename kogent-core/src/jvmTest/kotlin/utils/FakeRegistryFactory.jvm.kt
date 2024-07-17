package utils

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ralphdugue.kogent.cache.DataSourceRegistryDB

actual class TestDriverFactory {
    actual fun createTestDriver(): SqlDriver {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        DataSourceRegistryDB.Schema.create(driver)
        return driver
    }
}