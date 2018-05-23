package rekkit.viewmodels

import akme.State
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import arrow.effects.*
import rekkit.models.Commands
import rekkit.models.Commands.NavigationGoToUrlCommand
import rekkit.models.Commands.NavigationLoadNewsCommand
import rekkit.models.Notifications
import rekkit.models.ViewState
import rekkit.models.toDeferred
import rekkit.ui.commons.NavigationService

class NavigationModel(val navigationService: NavigationService): ViewModel() {

    val viewState = State<ViewState>(ViewState.IdleViewState)

    fun dispatch(command: Commands): DeferredK<*> {
        viewState.setValue(ViewState.IdleViewState)
        return when (command) {
            is NavigationLoadNewsCommand -> navigationService.loadNews()
            is NavigationGoToUrlCommand -> navigationService.goToWeb(command.url)
            else -> command.toDeferred()
        }
    }

    fun notification(notification: Notifications): DeferredK<Unit> = when (notification) {
        is Notifications.NavigationNotification -> DeferredK {
            viewState.postValue(ViewState.FailedViewState(notification.ex))
        }
        else -> notification.toDeferred()
    }

}

class NavigationModuleFactory(val navigationService: NavigationService) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NavigationModel(navigationService) as T
    }
}