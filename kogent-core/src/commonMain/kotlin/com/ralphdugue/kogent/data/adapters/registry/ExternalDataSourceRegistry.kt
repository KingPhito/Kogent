package com.ralphdugue.kogent.data.adapters.registry

import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.SQLDataSource

class ExternalDataSourceRegistry(dataSource: SQLDataSource) : DataSourceRegistry {
    override suspend fun getDataSources(): Result<List<DataSource>> {
        TODO("Not yet implemented")
    }

    override suspend fun registerDataSource(dataSource: DataSource): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getDataSourceById(identifier: String): Result<DataSource> {
        TODO("Not yet implemented")
    }

    override suspend fun removeDataSource(identifier: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDataSource(dataSource: DataSource): Result<Unit> {
        TODO("Not yet implemented")
    }
}