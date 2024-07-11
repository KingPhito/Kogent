package com.ralphdugue.kogent.data.adapters

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.ralphdugue.kogent.AppContextProvider
import com.ralphdugue.kogent.cache.DataSourceRegistryDB

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(url = DATASOURCE_REGISTRY_DB)
        DataSourceRegistryDB.Schema.create(driver)
        return driver
    }

    actual var appContextProvider: AppContextProvider? = null
}
