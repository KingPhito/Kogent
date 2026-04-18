package com.ralphdugue.kogent.indexing.adapters

import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig

expect fun buildMilvusIndex(config: IndexConfig.VectorStoreConfig): Index
