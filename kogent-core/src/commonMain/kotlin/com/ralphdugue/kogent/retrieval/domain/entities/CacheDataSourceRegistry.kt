package com.ralphdugue.kogent.retrieval.domain.entities

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource
import org.koin.core.annotation.Single

@Single
class CacheDataSourceRegistry : DataSourceRegistry {
    override fun getDataSources(): List<DataSource> {
        TODO("Not yet implemented")
    }

    override fun registerDataSource(dataSource: DataSource) {
        TODO("Not yet implemented")
    }
}
