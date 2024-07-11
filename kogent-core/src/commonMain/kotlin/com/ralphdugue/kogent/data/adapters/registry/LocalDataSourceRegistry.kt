package com.ralphdugue.kogent.data.adapters.registry

import com.ralphdugue.kogent.cache.DataSourceRegistryDB
import com.ralphdugue.kogent.data.domain.entities.DataSource
import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import org.koin.core.annotation.Single

class LocalDataSourceRegistry(database: DataSourceRegistryDB) : DataSourceRegistry {
    override suspend fun getDataSources(): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun registerDataSource(dataSource: DataSource) {
        TODO("Not yet implemented")
    }
}

