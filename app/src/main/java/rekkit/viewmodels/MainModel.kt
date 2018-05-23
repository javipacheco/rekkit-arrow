package rekkit.viewmodels

import akme.State
import android.arch.lifecycle.ViewModel
import arrow.effects.DeferredK
import rekkit.models.Commands
import rekkit.models.MainNavigationState
import rekkit.models.toDeferred

class MainModel : ViewModel() {

    val navigationState = State<MainNavigationState>(MainNavigationState.News)

    fun dispatch(command: Commands): DeferredK<Unit> = when (command) {
        is Commands.MainNavigationCommand -> DeferredK {
            navigationState.postValue(command.item)
        }
        else -> command.toDeferred()
    }

}