package com.ralphdugue.kogent.config

import com.ralphdugue.kogent.dataconnector.domain.entities.DataSource
import com.ralphdugue.kogent.dataconnector.domain.entities.EmbeddedRegistry
import com.ralphdugue.kogent.dataconnector.domain.entities.ExternalRegistryBuilder
import com.ralphdugue.kogent.dataconnector.domain.entities.RegistryType
import com.ralphdugue.kogent.dataconnector.domain.entities.api.ApiDataSourceBuilder
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.APIEmbeddingModelConfigBuilder
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.EmbeddingConfig
import com.ralphdugue.kogent.dataconnector.domain.entities.embedding.HuggingFaceEmbeddingModelConfigBuilder
import com.ralphdugue.kogent.dataconnector.domain.entities.sql.SQLDataSourceBuilder
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfigBuilder
import com.ralphdugue.kogent.query.domain.entities.HuggingFaceLLModelConfigBuilder
import com.ralphdugue.kogent.query.domain.entities.LLModelConfig

/**
 * The Kogent configuration.
 *
 * This class is used to configure the Kogent system. It contains information about the data sources,
 * the model configuration, the index configuration, and the embedding configuration.
 * @param dataSources The data sources to use in the Kogent system.
 * @param llModelConfig The model configuration.
 * @param indexConfig The index configuration.
 * @param embeddingConfig The embedding configuration.
 * @param registryType The type of registry to use. Default is [EmbeddedRegistry].
 */
data class KogentConfig(
    val dataSources: List<DataSource>,
    val llModelConfig: LLModelConfig,
    val indexConfig: IndexConfig,
    val embeddingConfig: EmbeddingConfig,
    val registryType: RegistryType = EmbeddedRegistry,
)

/**
 * A builder for the Kogent configuration.
 */
class KogentConfigBuilder {
    private var dataSources: MutableList<DataSource> = mutableListOf()
    private var llModelConfig: LLModelConfig? = null
    private var indexConfig: IndexConfig? = null
    private var embeddingConfig: EmbeddingConfig? = null
    private var registryType: RegistryType = EmbeddedRegistry

    fun sqlDataSource(block: SQLDataSourceBuilder.() -> Unit) {
        dataSources.add(SQLDataSourceBuilder().apply(block).build())
    }

    fun apiDataSource(block: ApiDataSourceBuilder.() -> Unit) {
        dataSources.add(ApiDataSourceBuilder().apply(block).build())
    }

    fun huggingFaceLLModelConfig(block: HuggingFaceLLModelConfigBuilder.() -> Unit) {
        llModelConfig = HuggingFaceLLModelConfigBuilder().apply(block).build()
    }

    fun vectorIndexConfig(block: VectorStoreConfigBuilder.() -> Unit) {
        indexConfig = VectorStoreConfigBuilder().apply(block).build()
    }

    fun huggingFaceEmbeddingConfig(block: HuggingFaceEmbeddingModelConfigBuilder.() -> Unit) {
        embeddingConfig = HuggingFaceEmbeddingModelConfigBuilder().apply(block).build()
    }

    fun apiEmbeddingConfig(block: APIEmbeddingModelConfigBuilder.() -> Unit) {
        embeddingConfig = APIEmbeddingModelConfigBuilder().apply(block).build()
    }

    fun externalRegistry(block: ExternalRegistryBuilder.() -> Unit) {
        registryType = ExternalRegistryBuilder().apply(block).build()
    }

    fun build(): KogentConfig =
        KogentConfig(
            dataSources = dataSources,
            llModelConfig = llModelConfig ?: throw IllegalStateException("llModelConfig must be set"),
            indexConfig = indexConfig ?: throw IllegalStateException("indexConfig must be set"),
            embeddingConfig = embeddingConfig ?: throw IllegalStateException("embeddingConfig must be set"),
            registryType = registryType,
        )
}
