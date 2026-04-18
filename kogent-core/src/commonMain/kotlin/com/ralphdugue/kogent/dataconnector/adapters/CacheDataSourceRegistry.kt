package com.ralphdugue.kogent.dataconnector.adapters

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.DataSourceRegistry
import org.koin.core.annotation.Single

@Single
class CacheDataSourceRegistry : DataSourceRegistry {
    override suspend fun getDataSources(): List<DataSource> {
        TODO("Not yet implemented")
    }

    override suspend fun registerDataSource(dataSource: DataSource) {
        TODO("Not yet implemented")
    }
}
