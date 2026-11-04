package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAspectRatioCycling() {
    val initial = com.example.player.AspectRatioMode.FIT
    val next = initial.next()
    assertEquals(com.example.player.AspectRatioMode.FILL, next)
  }

  @Test
  fun testThemeCount() {
    assertEquals(6, com.example.model.PlayerThemeId.entries.size)
  }
}
