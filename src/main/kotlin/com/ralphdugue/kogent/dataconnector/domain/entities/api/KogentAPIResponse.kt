package com.ralphdugue.kogent.dataconnector.domain.entities.api

sealed interface KogentAPIResponse<out T : Any> {
    data class Success<out T : Any>(
        val data: T?,
    ) : KogentAPIResponse<T>

    data class Error(
        val exception: Throwable,
    ) : KogentAPIResponse<Nothing>
}
