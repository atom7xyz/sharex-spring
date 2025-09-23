package xyz.atom7.sharexspring.config.init

interface Initializer {

    fun init()
    fun shouldInit(): Boolean

}