package sk.dusanbartos.grison.domain.logger

import kotlin.reflect.KClass

interface Logger {
    fun v(tag: KClass<*>, message: String)
    fun d(tag: KClass<*>, message: String)
    fun i(tag: KClass<*>, message: String)
    fun w(tag: KClass<*>, message: String)
    fun w(tag: KClass<*>, message: String, t: Throwable)
    fun e(tag: KClass<*>, message: String)
    fun e(tag: KClass<*>, message: String, t: Throwable)
    fun wtf(tag: KClass<*>, message: String, t: Throwable)

    object Empty : Logger {
        override fun v(tag: KClass<*>, message: String) {}
        override fun d(tag: KClass<*>, message: String) {}
        override fun i(tag: KClass<*>, message: String) {}
        override fun w(tag: KClass<*>, message: String) {}
        override fun w(tag: KClass<*>, message: String, t: Throwable) {}
        override fun e(tag: KClass<*>, message: String) {}
        override fun e(tag: KClass<*>, message: String, t: Throwable) {}
        override fun wtf(tag: KClass<*>, message: String, t: Throwable) {}
    }
}