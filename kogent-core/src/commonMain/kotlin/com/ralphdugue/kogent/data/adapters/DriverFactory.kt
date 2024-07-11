package com.ralphdugue.kogent.data.adapters

import app.cash.sqldelight.db.SqlDriver
import com.ralphdugue.kogent.AppContextProvider

expect class DriverFactory() {
    var appContextProvider: AppContextProvider?
    fun createDriver(): SqlDriver
}



const val DATASOURCE_REGISTRY_DB = "jdbc:sqlite:ds-registry.db"