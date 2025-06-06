package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI

sealed class TerminalAction : BaseAction() {
    // TODO
}

class TerminalViewModel() : BaseMVI<TerminalAction>() {

    override fun dispatch(action: TerminalAction) {
        // TODO
    }
}