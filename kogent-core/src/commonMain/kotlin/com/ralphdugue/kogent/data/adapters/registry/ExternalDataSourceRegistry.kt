package com.ralphdugue.kogent.data.adapters.registry

import com.ralphdugue.kogent.data.domain.entities.CacheDataSource
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataSource

class ExternalDataSourceRegistry(dataSource: SQLDataSource) : DataSourceRegistry {
    override suspend fun getDataSources(): List<CacheDataSource> {
        TODO("Not yet implemented")
    }

    override suspend fun registerDataSource(dataSource: DataSource) {
        TODO("Not yet implemented")
    }
}