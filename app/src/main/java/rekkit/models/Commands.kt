package rekkit.models

import akme.toDeferred
import arrow.effects.DeferredK

sealed class Commands {

    // Main
    data class MainNavigationCommand(val item: MainNavigationState) : Commands()

    // News
    data class NewsGetItemsCommand(val limit: Int = 10) : Commands()

    data class ReloadNewsCommand(val limit: Int = 10) : Commands()

    // Navigation
    object NavigationLoadNewsCommand : Commands()
    data class NavigationGoToUrlCommand(val url: String) : Commands()

}

fun Commands.toDeferred(): DeferredK<Unit> = "${this.javaClass.simpleName} command doesn't exist".toDeferred()