package com.badoo.ribs.core.routing.portal

import android.os.Parcelable
import com.badoo.ribs.core.Node

sealed class AncestryInfo {

    /**
     * The whole chain of Configurations that can be used to reach this [Node]
     */
    abstract val configurationChain: List<Parcelable>

    /**
     * Represents ancestry info of a [Node] that is a root of a tree and doesn't have any parents
     */
    object Root : AncestryInfo() {
        override var configurationChain: List<Parcelable> = emptyList()
    }

    /**
     * Represents ancestry info of a [Node] that is attached to a parent
     */
    data class Child(
        /**
         * The virtual parent of the current [Node]. In most cases it is the same as the actual parent,
         * however, it is different e.g. in the case of full screen [Portal]s: the actual parent is the
         * portal host Node, while the anchor refers to the [Node] that initiated building and adding
         * the current one to the [Portal].
         */
        val anchor: Node<*>,

        /**
         * The Configuration the parent [Node] was set to that resulted in the creation of this [Node].
         * Used in [configurationChain] for [Portal] resolution.
         */
        val creatorConfiguration: Parcelable
    ) : AncestryInfo() {

        override val configurationChain: List<Parcelable>
            get() = anchor.ancestryInfo.configurationChain + creatorConfiguration
    }
}
