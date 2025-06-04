package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseViewModel

sealed class TerminalAction : BaseAction() {
    // TODO
}

class TerminalViewModel : BaseViewModel<TerminalAction>() {

    override fun dispatch(action: io.github.adbhelper.app.viewmodel.TerminalAction) {
        // TODO
    }
}