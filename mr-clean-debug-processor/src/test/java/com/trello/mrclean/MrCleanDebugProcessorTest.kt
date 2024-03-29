package com.trello.mrclean

import com.google.common.truth.Truth.assertThat
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File

class MrCleanDebugProcessorTest {

    @OptIn(ExperimentalCompilerApi::class)
    @Test
    fun `debug default cleartext private string`() {
        val kotlinSource = SourceFile.kotlin(
            "KClass.kt",
            """
            package com.test
            import com.trello.mrclean.annotations.Sanitize

            @Sanitize
            data class EightParam(
                val one: Int,
                val two: Int,
                val three: Int,
                val four: Int,
                val five: Int,
                val six: Int,
                private val seven: Int,
                val eight: Int,
            )
    """,
        )
        val option = mapOf(
            "mrclean.packagename" to "com.test",
        )
        val compilation = KotlinCompilation().apply {
            sources = listOf(kotlinSource)

            // pass your own instance of an annotation processor
            symbolProcessorProviders = listOf(TestMrCleanProcessorProvider(option))

            inheritClassPath = true
            messageOutputStream = System.out // see diagnostics in real time
        }
        val result = compilation.compile()
        assertThat(result.exitCode).isEqualTo(ExitCode.OK)
        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile =
            File(generatedSourcesDir, "kotlin/com/test/SanitizationForTest.EightParam.kt")
        assertThat(generatedFile.exists()).isTrue()
        assertThat(
            generatedFile.readText().trimIndent(),
        ).isEqualTo(
            """
package com.test

import kotlin.String
import kotlin.Suppress

@Suppress("NOTHING_TO_INLINE")
internal inline fun EightParam.sanitizedToString(): String =
    ""${'"'}EightParam(one = ${'$'}one, two = ${'$'}two, three = ${'$'}three, four = ${'$'}four, five = ${'$'}five, six = ${'$'}six, seven = <private>, eight = ${'$'}eight)""${'"'}
            """.trimIndent().trim(),
        )
    }
}
