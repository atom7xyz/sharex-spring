package xyz.atom7.sharexspring.config.init

interface Initializer {

    fun init()
    fun shouldInit(): Boolean

    /**
     * This method executes always after the init(), even if shouldInit() returns false
     */
    fun then()

}