package rekkit.models

import akme.toDeferred
import arrow.effects.DeferredK

sealed class Notifications() {

    // Main
    data class NewsGetItemsNotification(val ex: Throwable) : Notifications()

    // Navigation
    data class NavigationNotification(val ex: Throwable) : Notifications()
}

fun Notifications.toDeferred(): DeferredK<Unit> = "${this.javaClass.simpleName} notification doesn't exist".toDeferred()