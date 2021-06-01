package com.droidsingh.daggertrack

/**
 * A dependency which will take more time on cpu
 */
class HeavyDependencyOne {
    init {
        for (i in 1..100000000) {
            Dependency(1)
        }
    }

    private data class Dependency(val number: Int)
}
