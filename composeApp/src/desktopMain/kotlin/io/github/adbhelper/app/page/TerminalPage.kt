package io.github.adbhelper.app.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.adbhelper.app.viewmodel.TerminalViewModel
import io.github.adbhelper.composeViewModel
import io.github.adbhelper.i18n.StringRes

@Composable
fun TerminalPage(
    model: TerminalViewModel = composeViewModel(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(StringRes.locale.pullRequest)
    }
}