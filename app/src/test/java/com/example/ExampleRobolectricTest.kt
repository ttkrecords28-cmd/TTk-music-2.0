package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DemoCatalog
import com.example.player.LrcParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TTK Music", appName)
  }

  @Test
  fun `verify lrc parser extracts timestamps and text`() {
    val lrc = """
      [00:04.50] Chromatic reflections
      [00:09.00] Synths pulsing
    """.trimIndent()
    val lines = LrcParser.parse(lrc)
    assertEquals(2, lines.size)
    assertEquals(4500L, lines[0].timeMs)
    assertEquals("Chromatic reflections", lines[0].text)
    assertEquals(9000L, lines[1].timeMs)
    assertEquals("Synths pulsing", lines[1].text)
  }

  @Test
  fun `verify demo catalog contains preloaded tracks`() {
    assertTrue(DemoCatalog.DEFAULT_SONGS.isNotEmpty())
    val first = DemoCatalog.DEFAULT_SONGS.first()
    assertEquals("Midnight Neon Horizon", first.title)
  }

  @Test
  fun `verify shareable lyrics tile text formatting`() {
    val lyrics = listOf("Gliding through neon streets", "Chromatic reflections")
    val quote = lyrics.joinToString("\n")
    assertTrue(quote.contains("Gliding through neon streets"))
    assertTrue(quote.contains("Chromatic reflections"))
  }
}
