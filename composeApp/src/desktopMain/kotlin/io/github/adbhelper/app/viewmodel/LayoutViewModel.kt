package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseViewModel

sealed class LayoutAction : BaseAction() {
    // TODO
}

class LayoutViewModel : BaseViewModel<LayoutAction>() {

    override fun dispatch(action: LayoutAction) {
        // TODO
    }
}