package com.ralphdugue.kogent.config

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig

data class KogentConfig(
    val dataSources: List<DataSource>,
    val llModelConfig: LLModelConfig,
    val indexConfig: IndexConfig,
    val embeddingConfig: EmbeddingConfig,
)
