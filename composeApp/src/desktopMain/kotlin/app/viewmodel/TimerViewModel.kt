package app.viewmodel

import mvi.BaseAction
import mvi.BaseViewModel

sealed class TimerAction : BaseAction() {
    // TODO
}

class TimerViewModel : BaseViewModel<TimerAction>() {

    override fun dispatch(action: TimerAction) {
        // TODO
    }
}