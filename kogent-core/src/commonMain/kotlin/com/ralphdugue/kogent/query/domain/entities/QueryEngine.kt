package com.ralphdugue.kogent.query.domain.entities

import com.ralphdugue.kogent.data.domain.entities.DataSource

interface QueryEngine {
    suspend fun sendUserQuery(query: String): String

    suspend fun addDataSource(dataSource: DataSource): Boolean

    suspend fun removeDataSource(dataSource: DataSource): Boolean

    suspend fun updateDataSource(dataSource: DataSource): Boolean
}
