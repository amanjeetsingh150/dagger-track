package me.amanjeet.daggertrack

/**
 * A dependency doing off cpu time at initialization
 */
class HeavyDependencyTwo {
    init {
        Thread.sleep(4000)
    }
}