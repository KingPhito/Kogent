package com.ralphdugue.kogent.dataconnector.domain.entities.api

import com.ralphdugue.kogent.dataconnector.domain.entities.DataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource

interface APIDataConnector : DataConnector<KogentAPIResponse<String>> {
    suspend fun fetchData(dataSource: DataSource): KogentAPIResponse<String>

    suspend fun postData(
        dataSource: DataSource,
        data: String,
    ): KogentAPIResponse<String>
}
