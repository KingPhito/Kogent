package com.ralphdugue.kogent.indexing.domain.entities

sealed interface Document {
    val id: String
    val sourceType: String
    val sourceName: String
    val embedding: FloatArray

    data class SQLDocument(
        override val id: String,
        override val sourceType: String,
        override val sourceName: String,
        val dialect: String,
        override val embedding: FloatArray,
    ) : Document

    data class APIDocument(
        override val id: String,
        override val sourceType: String,
        override val sourceName: String,
        override val embedding: FloatArray,
    ) : Document
}
