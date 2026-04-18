package com.ralphdugue.kogent.query.domain.entities

import kotlinx.serialization.Serializable

/**
 *
 */
@Serializable
data class LLMResponse(
    val userResponse: String,
)
