package com.ralphdugue.kogent.dataconnector.domain.entities

interface KogentEmbeddingModel {
    suspend fun getEmbedding(text: String): FloatArray
}
