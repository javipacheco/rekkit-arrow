package rekkit.ui.main

import akme.*
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import rekkit.R
import rekkit.viewmodels.NavigationModel
import rekkit.models.Commands.NavigationGoToUrlCommand
import rekkit.models.Commands.NavigationLoadNewsCommand
import rekkit.ui.commons.NavigationService
import rekkit.ui.main.MainMessageItems.ItemNotFoundMessage
import rekkit.ui.main.MainMessageItems.NavigationErrorMessage
import rekkit.models.MainNavigationState.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import rekkit.viewmodels.MainModel
import rekkit.models.Commands
import rekkit.models.MainNavigationState
import rekkit.models.Notifications
import rekkit.models.ViewState
import rekkit.viewmodels.NavigationModuleFactory

class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        NavigationService {

    private val navigationModel: NavigationModel by lazy {
        ViewModelProviders.of(this, NavigationModuleFactory(this)).get(NavigationModel::class.java)
    }

    private val mainModel: MainModel by lazy {
        ViewModelProviders.of(this).get(MainModel::class.java)
    }

    override fun getActivity(): Activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationModel.viewState.observe(this, Observer {
            when (it) {
                is ViewState.FailedViewState -> showMessage(NavigationErrorMessage)
            }
        })

        mainModel.navigationState.observe(this, Observer {
            it?.let { navigation(it) }
        })

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        nav_view.setCheckedItem(News.id)
        nav_view.getMenu().performIdentifierAction(News.id, 0);

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mainModel.dispatch(Commands.MainNavigationCommand(MainNavigationState.toNavigationItem(item.itemId))).unsafeRunAsyncWithLog()
        return true
    }

    private fun navigation(item: MainNavigationState) {
        when (item) {
            News ->
                navigationModel.dispatch(NavigationLoadNewsCommand).unsafeRunAsyncWithException {
                    navigationModel.notification(Notifications.NavigationNotification(it))
                }
            GitHub ->
                navigationModel.dispatch(NavigationGoToUrlCommand(getString(R.string.github_url))).unsafeRunAsyncWithException {
                    navigationModel.notification(Notifications.NavigationNotification(it))
                }
            NotFound ->
                showMessage(ItemNotFoundMessage)
        }
        closeDrawer()
    }

    private fun showMessage(item: MainMessageItems) {
        val msg = when (item) {
            NavigationErrorMessage -> getString(R.string.navigationError)
            ItemNotFoundMessage -> getString(R.string.itemNotFoundError)
        }
        drawer_layout.longSnackbar(msg)
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

}
