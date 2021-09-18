package me.amanjeet.daggertrack

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

class DaggerTrackConfigTest {

    @Test
    fun `Dagger Track ConfigBuilder function matches each dagger track config properties`() {
        assertThat(daggerConfigProperties())
            .containsExactlyElementsIn(daggerTrackConfigBuilderFunctions())
    }

    private fun daggerConfigProperties(): List<String> {
        return DaggerTrack.Config::class.memberProperties
            .map { it.name }
    }

    private fun daggerTrackConfigBuilderFunctions(): Set<String> {
        return DaggerTrack.Config.Builder::class.memberFunctions
            .map { it.name }
            .subtract(listOf("build", "equals", "hashCode", "toString"))
    }
}