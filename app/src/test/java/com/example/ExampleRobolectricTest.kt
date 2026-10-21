package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CharacterClass
import com.example.model.Hero
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
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("The Lost Kingdom", appName)
  }

  @Test
  fun `hero creation and combat stats calculation`() {
    val warrior = Hero.createDefault("Cedric", CharacterClass.WARRIOR)
    assertEquals("Cedric", warrior.name)
    assertEquals(CharacterClass.WARRIOR, warrior.heroClass)
    assertTrue(warrior.totalAttack > 0)
    assertTrue(warrior.totalDefense > 0)
  }
}
