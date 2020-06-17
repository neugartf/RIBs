package com.badoo.ribs.sandbox.rib.compose_parent

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.Composable
import androidx.compose.Recomposer
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.sandbox.rib.compose_parent.ComposeParentView.Event
import com.badoo.ribs.sandbox.rib.compose_parent.ComposeParentView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface ComposeParentView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event

    data class ViewModel(
        val i: Int = 0
    )

    interface Factory : ViewFactory<Nothing?, ComposeParentView>
}


class ComposeParentViewImpl private constructor(
    context: Context,
    private val composable: @Composable() (ViewModel) -> Unit,
    private val events: PublishRelay<ComposeParentView.Event> = PublishRelay.create()
) : ComposeParentView,
    ObservableSource<ComposeParentView.Event> by events,
    Consumer<ViewModel> {

    class Factory(
        private val composable: @Composable() (ViewModel) -> Unit = { Content(it) }
    ) : ComposeParentView.Factory {
        override fun invoke(deps: Nothing?): (ViewGroup) -> ComposeParentView = {
            ComposeParentViewImpl(
                it.context,
                composable
            )
        }
    }

    override val androidView: ViewGroup = FrameLayout(context)

    override fun accept(vm: ViewModel) {
        androidView.setContent(Recomposer.current()) {
            composable(vm)
        }
    }
}

@Composable
fun Content(vm: ViewModel) {
    Column {
        Text(text = "ComposeParentView")
    }
}