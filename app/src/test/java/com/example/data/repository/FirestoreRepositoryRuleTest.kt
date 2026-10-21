package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.FirestoreRepository
import com.example.model.CharacterClass
import com.example.model.Hero
import com.example.model.ZoneType
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndLoadCloudHero_authenticatedUser_succeeds() = runBlocking {
    signInTestUser(ALICE_EMAIL)
    val repo = FirestoreRepository(firestore)

    val hero = Hero.createDefault("Sir Cedric", CharacterClass.WARRIOR)
    val result = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.saveHeroToCloud(
        slotIndex = 0,
        hero = hero,
        zone = ZoneType.OAKHAVEN_VILLAGE,
        savePayloadJson = "{\"slot\": 0}"
      )
    }
    assertTrue("Save should succeed", result.isSuccess)

    val loadedResult = withTimeout(DEFAULT_TIMEOUT_MS) {
      repo.loadHeroFromCloud(slotIndex = 0)
    }
    assertTrue("Load should succeed", loadedResult.isSuccess)
    val doc = loadedResult.getOrNull()
    assertNotNull(doc)
    assertEquals("Sir Cedric", doc?.heroName)
  }

  @Test
  fun loadCloudHero_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = FirestoreRepository(firestore)
    val hero = Hero.createDefault("Sir Cedric", CharacterClass.WARRIOR)
    withTimeout(DEFAULT_TIMEOUT_MS) {
      aliceRepo.saveHeroToCloud(0, hero, ZoneType.OAKHAVEN_VILLAGE, "{}")
    }

    // Now sign in as Bob and attempt to read Alice's save directly
    signInTestUser(BOB_EMAIL)
    try {
      withTimeout(DEFAULT_TIMEOUT_MS) {
        firestore.collection("users").document(aliceUid).collection("saves").document("slot_0").get().await()
      }
      fail("Should have failed with PERMISSION_DENIED")
    } catch (e: FirebaseFirestoreException) {
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
    }
  }

  @Test
  fun observeLeaderboard_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repo = FirestoreRepository(firestore)

    try {
      withTimeout(3000L) {
        repo.observeLeaderboard().first()
      }
      fail("Should have failed with PERMISSION_DENIED")
    } catch (e: Exception) {
      val rootCause = generateSequence<Throwable>(e) { it.cause }.firstOrNull { it is FirebaseFirestoreException } as? FirebaseFirestoreException
      assertNotNull("Expected FirebaseFirestoreException in cause chain", rootCause)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, rootCause?.code)
    }
  }

  private companion object {
    const val ALICE_EMAIL = "alice@test.com"
    const val BOB_EMAIL = "bob@test.com"
    const val DEFAULT_TIMEOUT_MS = 5000L
  }
}
