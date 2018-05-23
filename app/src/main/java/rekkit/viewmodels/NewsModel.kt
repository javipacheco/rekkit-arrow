package rekkit.viewmodels

import akme.State
import akme.logD
import rekkit.models.toDeferred
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import arrow.effects.*
import arrow.effects.monad
import arrow.typeclasses.binding
import rekkit.models.Commands
import rekkit.models.Notifications
import rekkit.models.States
import rekkit.models.ViewState
import rekkit.services.ApiService

class NewsModel(val apiService: ApiService) : ViewModel() {

    val newsState = State<States.NewsState>()

    val viewState = State<ViewState>(ViewState.IdleViewState)

    fun dispatch(command: Commands): DeferredK<Unit> = when (command) {
        is Commands.NewsGetItemsCommand -> {
            DeferredK.monad().binding {
                val newsList = newsState.getValue()
                if (newsList?.items?.isEmpty() == false) {
                    "Loading data from state".logD()
                    viewState.postValue(ViewState.SuccessViewState)
                    newsState.postValue(newsList)
                } else {
                    viewState.postValue(ViewState.WaitingViewState)
                    dispatch(Commands.ReloadNewsCommand(command.limit)).bind()
                }
                Unit
            }.fix()
        }
        is Commands.ReloadNewsCommand -> {
            DeferredK.monad().binding {
                "Loading new data".logD()
                val newsList = newsState.getValue()
                val before: String? = newsList?.items?.firstOrNull()?.name
                val news = apiService.getNews(command.limit, before).bind()
                viewState.postValue(ViewState.SuccessViewState)
                newsState.postValue(newsList?.copy(items = news + newsList.items)
                        ?: States.NewsState(news))
                Unit
            }.fix()
        }
        else -> command.toDeferred()
    }

    fun notification(notification: Notifications): DeferredK<Unit> = when (notification) {
        is Notifications.NewsGetItemsNotification -> DeferredK {
            viewState.postValue(ViewState.FailedViewState(notification.ex))
        }
        else -> notification.toDeferred()
    }

}

class NewsModuleFactory(val apiService: ApiService) :
        ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsModel(apiService) as T
    }
}
