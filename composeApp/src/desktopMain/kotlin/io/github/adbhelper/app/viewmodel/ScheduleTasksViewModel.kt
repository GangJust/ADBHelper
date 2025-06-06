package io.github.adbhelper.app.viewmodel

import io.github.adbhelper.adb.entity.Device
import io.github.adbhelper.mvi.BaseAction
import io.github.adbhelper.mvi.BaseMVI

sealed class TimerAction : BaseAction() {
    // TODO
}

class ScheduleTasksViewModel() : BaseMVI<TimerAction>() {

    override fun dispatch(action: TimerAction) {
        // TODO
    }
}