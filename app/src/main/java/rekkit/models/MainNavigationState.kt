package rekkit.models

import rekkit.R

sealed class MainNavigationState(val id: Int) {

    object News : MainNavigationState(R.id.nav_news)
    object GitHub : MainNavigationState(R.id.nav_github)
    object NotFound : MainNavigationState(0)

    companion object {

        fun toNavigationItem(itemId: Int): MainNavigationState = when (itemId) {
            News.id -> News
            GitHub.id -> GitHub
            else -> NotFound
        }

    }

}