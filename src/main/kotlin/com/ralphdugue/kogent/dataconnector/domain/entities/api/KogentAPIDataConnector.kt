package com.ralphdugue.kogent.dataconnector.domain.entities.api

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataConnector
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSource

interface KogentAPIDataConnector : KogentDataConnector<KogentAPIResponse<String>> {
    override suspend fun fetchData(dataSource: KogentDataSource): KogentAPIResponse<String>

    suspend fun postData(
        dataSource: KogentDataSource,
        data: String,
    ): KogentAPIResponse<String>
}
