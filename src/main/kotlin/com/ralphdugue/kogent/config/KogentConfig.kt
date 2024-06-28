package com.ralphdugue.kogent.config

import com.ralphdugue.kogent.dataconnector.domain.entities.KogentDataSourceCollection
import com.ralphdugue.kogent.dataconnector.domain.entities.KogentEmbeddingModel
import com.ralphdugue.kogent.indexing.domain.entities.KogentIndexConfig
import com.ralphdugue.kogent.query.domain.entities.KogentLLModel

data class KogentConfig(
    val dataSources: KogentDataSourceCollection,
    val llModel: KogentLLModel,
    val indexConfig: KogentIndexConfig,
    val embeddingModel: KogentEmbeddingModel? = null,
)
