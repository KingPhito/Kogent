package com.ralphdugue.kogent.data.domain.entities.api

import com.ralphdugue.kogent.data.domain.entities.APIDataSource
import com.ralphdugue.kogent.data.domain.entities.DataConnector

interface APIDataConnector : DataConnector {
    suspend fun fetchData(dataSource: APIDataSource): Result<String>

    suspend fun postData(
        dataSource: APIDataSource,
        data: String?,
    ): Result<String>
}
