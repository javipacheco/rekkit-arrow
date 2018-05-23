package rekkit.models

sealed class ViewState {

    object IdleViewState : ViewState()

    object WaitingViewState : ViewState()

    object SuccessViewState : ViewState()

    data class FailedViewState(val throwable: Throwable) : ViewState()

}