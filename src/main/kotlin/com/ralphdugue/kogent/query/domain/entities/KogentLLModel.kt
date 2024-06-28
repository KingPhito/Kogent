package com.ralphdugue.kogent.query.domain.entities

interface KogentLLModel {
    val type: KogentLLModelType

    suspend fun query(text: String): String
}

enum class KogentLLModelType {
    BERT,
    GPT3,
}
