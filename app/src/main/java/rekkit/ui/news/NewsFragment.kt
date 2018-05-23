package rekkit.ui.news

import akme.*
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.news_fragment.*
import rekkit.R
import rekkit.models.Commands
import rekkit.models.Commands.NavigationGoToUrlCommand
import rekkit.models.Commands.NewsGetItemsCommand
import rekkit.models.Notifications
import rekkit.models.States
import rekkit.models.ViewState
import rekkit.viewmodels.NavigationModel
import rekkit.viewmodels.NewsModel
import rekkit.viewmodels.NewsModuleFactory
import rekkit.services.ApiService
import rekkit.ui.commons.NavigationService
import rekkit.ui.news.NewsMessageItems.ErrorLoadingNewsMessage
import rekkit.ui.news.NewsMessageItems.NoNewsMessage
import rekkit.ui.news.adapters.NewsAdapter

class NewsFragment :
        Fragment(),
        NavigationService,
        SwipeRefreshLayout.OnRefreshListener {

    private val navigationModel = NavigationModel(this)

    private val newsModel: NewsModel by lazy {
        ViewModelProviders.of(this, NewsModuleFactory(ApiService())).get(NewsModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        newsModel.viewState.observe(this, Observer {
            when (it) {
                ViewState.WaitingViewState -> showLoading()
                ViewState.SuccessViewState -> showContent()
                is ViewState.FailedViewState -> showMessage(ErrorLoadingNewsMessage)
            }

        })
        newsModel.newsState.observe(this, Observer {
            it?.let { showNews(it.items) }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.news_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler.layoutManager = LinearLayoutManager(activity)
        swipe_refresh.setOnRefreshListener(this)
        newsModel.dispatch(NewsGetItemsCommand()).unsafeRunAsyncWithException {
            newsModel.notification(Notifications.NewsGetItemsNotification(it))
        }
    }

    override fun onRefresh() {
        newsModel.dispatch(Commands.ReloadNewsCommand()).unsafeRunAsyncWithException {
            newsModel.notification(Notifications.NewsGetItemsNotification(it))
        }
    }

    private fun showLoading() {
        progress_bar.visible()
        recycler.gone()
    }

    private fun showContent() {
        progress_bar.gone()
        recycler.visible()
    }

    private fun showNews(items: List<States.NewsItemState>) {
        if (items.isEmpty()) {
            showMessage(NoNewsMessage)
        } else {

            if (recycler.adapter != null) {
                (recycler.adapter as NewsAdapter).addAfterItems(items)
            } else {
                recycler.adapter = NewsAdapter(items) { url ->
                    navigationModel.dispatch(NavigationGoToUrlCommand(url)).unsafeRunAsyncWithException {
                        navigationModel.notification(Notifications.NavigationNotification(it))
                    }
                }
            }
        }
        swipe_refresh.isRefreshing = false
    }

    private fun showMessage(item: NewsMessageItems) {
        val msg = when (item) {
            NoNewsMessage -> getString(R.string.noMoreNews)
            ErrorLoadingNewsMessage -> getString(R.string.loadingNewsError)
        }
        swipe_refresh.longSnackbar(msg)
    }

}