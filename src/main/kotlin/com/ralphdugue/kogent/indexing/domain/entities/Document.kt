package com.ralphdugue.kogent.indexing.domain.entities

sealed interface Document {
    val id: String
    val sourceType: String
    val sourceName: String
    val embedding: List<FloatArray>

    data class SQLDocument(
        override val id: String,
        override val sourceType: String = "SQL",
        override val sourceName: String,
        val dialect: String,
        override val embedding: List<FloatArray>,
    ) : Document

    data class APIDocument(
        override val id: String,
        override val sourceType: String = "API",
        override val sourceName: String,
        override val embedding: List<FloatArray>,
    ) : Document
}
