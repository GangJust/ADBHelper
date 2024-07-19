package app.viewmodel

import mvi.BaseAction
import mvi.BaseViewModel

sealed class TerminalAction : BaseAction() {
    // TODO
}

class TerminalViewModel : BaseViewModel<TerminalAction>() {

    override fun dispatch(action: TerminalAction) {
        // TODO
    }
}