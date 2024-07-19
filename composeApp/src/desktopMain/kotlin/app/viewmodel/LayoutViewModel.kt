package app.viewmodel

import mvi.BaseAction
import mvi.BaseViewModel

sealed class LayoutAction : BaseAction() {
    // TODO
}

class LayoutViewModel : BaseViewModel<LayoutAction>() {

    override fun dispatch(action: LayoutAction) {
        // TODO
    }
}