package com.badoo.ribs.core.routing.configuration

import android.os.Parcelable
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeature
import com.badoo.ribs.core.routing.configuration.feature.BackStackFeatureState
import com.badoo.ribs.core.routing.configuration.feature.TransitionDescriptor
import com.badoo.ribs.core.routing.history.Routing
import com.badoo.ribs.core.routing.history.RoutingHistory
import com.badoo.ribs.core.routing.history.RoutingHistoryDiffer
import com.badoo.ribs.util.RIBs
import io.reactivex.Observable
import io.reactivex.ObservableSource

/**
 * Takes the state emissions from [BackStackFeature], and translates them to a stream of
 * [ConfigurationCommand]s.
 *
 * @see [ConfigurationCommandCreator.diff]
 */
internal fun <C : Parcelable> ObservableSource<RoutingHistory<C>>.toCommands(): Observable<Transaction<C>> =
    Observable.wrap(this)
        // FIXME this was the original and working one, but signature change means initialState is not accessible
//        .startWith(initialState)

        // FIXME temp solution, but this won't be good when restoring from bundle
        .startWith(BackStackFeatureState()) // TODO reconsider // Bootstrapper can overwrite it by the time we receive the first state emission here

        .buffer(2, 1)
        .flatMap { (previous, current) ->
            current.ensureUniqueIds()

            val commands =
                RoutingHistoryDiffer.diff(
                    previous,
                    current
                )
            when {
                commands.isNotEmpty() -> Observable.just(
                    Transaction.ListOfCommands(
                        descriptor = TransitionDescriptor(
                            from = previous,
                            to = current
                        ),
                        commands = commands.toList() // TODO consider to rename ListOfCommands to SetOfCommands?
                    )
                )
                else -> Observable.empty()
            }
        }

internal fun <C : Parcelable> RoutingHistory<C>.ensureUniqueIds() {
    val ids = mutableSetOf<Routing.Identifier>()
    forEach { element ->
        if (ids.contains(element.routing.identifier)) {
            val errorMessage = "Non-unique content id found: ${element.routing.identifier}"
            RIBs.errorHandler.handleNonFatalError(errorMessage, NonUniqueRoutingIdentifierException(errorMessage))
        }
        ids.add(element.routing.identifier)

        element.overlays.forEach { overlay ->
            if (ids.contains(overlay.identifier)) {
                val errorMessage = "Non-unique overlay id found: ${overlay.identifier}"
                RIBs.errorHandler.handleNonFatalError(errorMessage, NonUniqueRoutingIdentifierException(errorMessage))
            }
            ids.add(overlay.identifier)
        }
    }
}

class NonUniqueRoutingIdentifierException(message: String?) : RuntimeException(message)
