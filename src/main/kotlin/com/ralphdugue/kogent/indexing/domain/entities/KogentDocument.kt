package com.ralphdugue.kogent.indexing.domain.entities

sealed interface KogentDocument {
    val id: String
    val sourceType: String
    val sourceName: String
    val embedding: FloatArray

    data class KogentSQLDocument(
        override val id: String,
        override val sourceType: String,
        override val sourceName: String,
        val dialect: String,
        override val embedding: FloatArray,
    ) : KogentDocument

    data class KogentAPIDocument(
        override val id: String,
        override val sourceType: String,
        override val sourceName: String,
        override val embedding: FloatArray,
    ) : KogentDocument
}
