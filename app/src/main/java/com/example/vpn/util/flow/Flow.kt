package com.example.vpn.util.flow

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

/**
 * Utility extension function to start collecting a flow when the lifecycle is started,
 * and *cancel* the collection on stop, with a custom collector.
 * This is different from `lifecycleScope.launchWhenStarted{ flow.collect{..} }`, in which case the
 * coroutine is just suspended on stop.
 */
inline fun <reified T> Flow<T>.collectWhileStarted(
    lifecycleOwner: LifecycleOwner,
    noinline collector: suspend (T) -> Unit
) {
    ObserverWhileStartedImpl(lifecycleOwner, this, collector)
}

/**
 * Utility extension function on [Flow] to start collecting a flow when the lifecycle is started,
 * and *cancel* the collection on stop.
 */
inline fun <reified T> Flow<T>.collectWhileStartedIn(
    lifecycleOwner: LifecycleOwner
) {
    ObserverWhileStartedImpl(lifecycleOwner, this, {})
}

@PublishedApi
internal class ObserverWhileStartedImpl<T>(
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) : DefaultLifecycleObserver {

    private var job: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        job = owner.lifecycleScope.launchWhenStarted {
            flow.collect {
                collector(it)
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }
}

/**
 * Utility extension function to start collecting a flow when the lifecycle is resumed,
 * and *cancel* the collection on stop, with a custom collector.
 * This is different from `lifecycleScope.launchWhenResumed{ flow.collect{..} }`, in which case the
 * coroutine is just suspended on stop.
 */
inline fun <reified T> Flow<T>.collectWhileResumed(
    lifecycleOwner: LifecycleOwner,
    noinline collector: suspend (T) -> Unit
) {
    ObserverWhileResumedImpl(lifecycleOwner, this, collector)
}

/**
 * Utility extension function on [Flow] to start collecting a flow when the lifecycle is resumed,
 * and *cancel* the collection on stop.
 */
inline fun <reified T> Flow<T>.collectWhileResumedIn(
    lifecycleOwner: LifecycleOwner
) {
    ObserverWhileResumedImpl(lifecycleOwner, this, {})
}

@PublishedApi
internal class ObserverWhileResumedImpl<T>(
    lifecycleOwner: LifecycleOwner,
    private val flow: Flow<T>,
    private val collector: suspend (T) -> Unit
) : DefaultLifecycleObserver {

    private var job: Job? = null

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        job = owner.lifecycleScope.launchWhenResumed {
            flow.collect {
                collector(it)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }
}
