package sk.dusanbartos.grison.logger

import sk.dusanbartos.grison.domain.logger.Logger
import timber.log.Timber
import kotlin.reflect.KClass

/**
 * [Timber.DebugTree] wrapper
 */
class TimberDebugLogger() : Logger {

    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun v(tag: KClass<*>, message: String) = prepare(tag).v(message)
    override fun d(tag: KClass<*>, message: String) = prepare(tag).d(message)
    override fun i(tag: KClass<*>, message: String) = prepare(tag).i(message)
    override fun w(tag: KClass<*>, message: String) = prepare(tag).w(message)
    override fun w(tag: KClass<*>, message: String, t: Throwable) = prepare(tag).w(t, message)
    override fun e(tag: KClass<*>, message: String) = prepare(tag).e(message)
    override fun e(tag: KClass<*>, message: String, t: Throwable) = prepare(tag).e(t, message)
    override fun wtf(tag: KClass<*>, message: String, t: Throwable) = prepare(tag).wtf(t, message)
    private fun prepare(tag: KClass<*>): Timber.Tree = Timber.tag(tag.simpleName ?: "-")
}