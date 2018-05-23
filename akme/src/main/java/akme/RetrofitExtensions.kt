package akme

import arrow.effects.DeferredK
import arrow.effects.handleErrorWith
import arrow.effects.k
import kotlinx.coroutines.experimental.async
import retrofit2.Call

fun <T> Call<T>.toDeferredK(): DeferredK<T> = async {
    execute()
}.k().map {
    val body = it.body()
    if (it.isSuccessful && body != null) {
        body
    } else {
        throw AkmeException.ApiException("No body")
    }
}.handleErrorWith { DeferredK.raiseError(AkmeException.ApiException("Unexpected error")) }