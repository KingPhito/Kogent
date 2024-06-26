package indexing.domain.entities

import dataconnector.domain.entities.KogentQueryResult

sealed interface KogentDocument {
    val id: String
    val sourceType: String
    val sourceName: String

    data class KogentSQLDocument(
        override val id: String,
        override val sourceType: String,
        override val sourceName: String,
        val content: KogentQueryResult,
    ) : KogentDocument
}
