package com.ralphdugue.kogent.data.adapters.connectors

import com.ralphdugue.kogent.data.domain.entities.DataSourceRegistry
import com.ralphdugue.kogent.data.domain.entities.embedding.EmbeddingModel
import com.ralphdugue.kogent.data.domain.entities.sql.SQLDataConnector
import com.ralphdugue.kogent.indexing.domain.entities.Index

expect fun buildSQLDataConnector(
    embeddingModel: EmbeddingModel,
    index: Index,
    dataSourceRegistry: DataSourceRegistry,
): SQLDataConnector
