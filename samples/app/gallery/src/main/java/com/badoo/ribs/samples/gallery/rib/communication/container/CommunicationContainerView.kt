package com.badoo.ribs.samples.gallery.rib.communication.container

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.badoo.ribs.compose.ComposeRibView
import com.badoo.ribs.compose.ComposeView
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.core.view.ViewFactoryBuilder

interface CommunicationContainerView : RibView {

    fun interface Factory : ViewFactoryBuilder<Nothing?, CommunicationContainerView>
}


class CommunicationContainerViewImpl private constructor(
    context: Context
) : ComposeRibView(context),
    CommunicationContainerView {

    class Factory : CommunicationContainerView.Factory {
        override fun invoke(deps: Nothing?): ViewFactory<CommunicationContainerView> = ViewFactory {
            CommunicationContainerViewImpl(
                it.parent.context
            )
        }
    }

    private var content: MutableState<ComposeView?> = mutableStateOf(null)

    override fun getParentViewForSubtree(subtreeOf: Node<*>): MutableState<ComposeView?> =
        content

    override val composable: @Composable () -> Unit = {
        View(content.value)
    }
}

@Preview
@Composable
private fun View(
    child: ComposeView? = null
) {
    child?.invoke()
}
