package com.ralphdugue.kogent.data.adapters

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.ralphdugue.kogent.AppContextProvider
import com.ralphdugue.kogent.cache.DataSourceRegistryDB

actual class DriverFactory  {
    actual var appContextProvider: AppContextProvider? = null

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = DataSourceRegistryDB.Schema,
            context = appContextProvider?.getContext() as Context,
            name = DATASOURCE_REGISTRY_DB
        )
    }

}

