package com.ralphdugue.kogent.retrieval.utils

import com.ralphdugue.kogent.indexing.domain.entities.Document

object RetrieverUtils {

    fun buildContext(
        query: String,
        documents: List<Document>,
    ): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendLine("User request: $query\n")
        documents.forEach { document ->
            stringBuilder.appendLine("Document: ${document.id}")
            stringBuilder.appendLine("SourceType: ${document.sourceType}")
            stringBuilder.appendLine("SourceName: ${document.sourceName}")
            when (document) {
                is Document.SQLDocument -> {
                    stringBuilder.appendLine("Dialect: ${document.dialect}")
                    stringBuilder.appendLine("Schema: ${document.schema}")
                    stringBuilder.appendLine("Query: ${document.query}")
                }
                is Document.APIDocument -> {
                    stringBuilder.appendLine("Base URL: ${document.baseUrl}")
                    stringBuilder.appendLine("Endpoint: ${document.endpoint}")
                }
            }
            stringBuilder.appendLine("Text: ${document.text}")
            stringBuilder.appendLine("-----------------------------")
        }
        return stringBuilder.toString()
    }
}