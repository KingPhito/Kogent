package com.ralphdugue.kogent.retrieval.domain.entities

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource

interface DataSourceRegistry {
    fun getDataSourceNames(): List<String>

    fun registerDataSource(dataSource: DataSource)
}
