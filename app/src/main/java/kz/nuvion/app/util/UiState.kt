package kz.nuvion.app.util

import kotlinx.coroutines.CancellationException

/** Loading / content / error wrapper used by every screen. */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val kind: ErrorKind) : UiState<Nothing>
}

enum class ErrorKind { NOT_FOUND, UNKNOWN }

inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    is UiState.Error -> UiState.Error(kind)
    UiState.Loading -> UiState.Loading
}

/**
 * Runs [block] and maps the result to a [UiState]. A null result means "not found".
 * Coroutine cancellation is re-thrown so it is never reported as an error.
 */
suspend fun <T : Any> loadState(block: suspend () -> T?): UiState<T> = try {
    block()?.let { UiState.Success(it) } ?: UiState.Error(ErrorKind.NOT_FOUND)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    UiState.Error(ErrorKind.UNKNOWN)
}
