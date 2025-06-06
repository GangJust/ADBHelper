package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI

sealed class LayoutAction : BaseAction() {
    // TODO
}

class LayoutAnalysisViewModel() : BaseMVI<LayoutAction>() {

    override fun dispatch(action: LayoutAction) {
        // TODO
    }
}