package com.ralphdugue.kogent.data.adapters.registry

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry

class LocalDataSourceRegistry(private val database: DataSourceRegistryDB) : DataSourceRegistry {
    override suspend fun getDataSources(): List<DataSource> {
        TODO()
    }

    override suspend fun registerDataSource(dataSource: DataSource) {
        TODO()
    }
}

