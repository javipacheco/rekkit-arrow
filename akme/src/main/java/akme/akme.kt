package akme

import arrow.effects.*
import kotlinx.coroutines.experimental.Deferred

sealed class AkmeException: Throwable() {

    data class ApiException(val msg: String) : AkmeException()

    data class CallException(val msg: String) : AkmeException()

    data class NavigationException(val msg: String) : AkmeException()

}

fun String.toDeferred(): DeferredK<Unit> = DeferredK {
    this.logD()
    Unit
}

fun <A> DeferredK<A>.unsafeRunAsyncWithLog(message: String? = null): Unit = this.unsafeRunAsync {
    it.mapLeft {
        ((message ?: it.message) ?: ("Error!")).logE(throwable = it.cause)
    }
}

fun <A> DeferredK<A>.unsafeRunAsyncWithException(ex: ((Throwable) -> Unit)): Unit = this.unsafeRunAsync {
    it.mapLeft {
        ex(it)
    }
}

fun <A> Deferred<A>.orException(ex: AkmeException): DeferredK<A> = k().handleErrorWith {
    ex.initCause(it)
    DeferredK.raiseError(ex)
}

fun <A> (() -> A).orException(ex: AkmeException): DeferredK<A> = DeferredK.just(this()).handleErrorWith {
    ex.initCause(it)
    DeferredK.raiseError(ex)
}