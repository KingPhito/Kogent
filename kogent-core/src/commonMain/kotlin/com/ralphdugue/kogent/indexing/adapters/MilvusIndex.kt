package com.ralphdugue.kogent.indexing.adapters

import com.ralphdugue.kogent.indexing.domain.entities.Index
import com.ralphdugue.kogent.indexing.domain.entities.IndexConfig
import com.ralphdugue.kogent.indexing.domain.entities.VectorStoreConfig

expect fun buildMilvusIndex(config: VectorStoreConfig): Index
