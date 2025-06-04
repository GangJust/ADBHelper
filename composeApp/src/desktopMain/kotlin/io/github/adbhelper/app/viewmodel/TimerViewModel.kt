package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseViewModel

sealed class TimerAction : BaseAction() {
    // TODO
}

class TimerViewModel : BaseViewModel<TimerAction>() {

    override fun dispatch(action: TimerAction) {
        // TODO
    }
}