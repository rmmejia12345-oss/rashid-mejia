package com.example.data.firestore

import android.content.Context
import com.example.R
import com.example.model.Hero
import com.example.model.ZoneType
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore
) {
    // Required secondary constructor taking Context and resolving named database ID
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            FirebaseApp.getInstance(),
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    // Cloud Saves
    fun observeUserSaves(): Flow<List<UserSaveDoc>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/saves"
        emitAll(
            db.collection("users").document(uid).collection("saves")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(UserSaveDoc::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun saveHeroToCloud(
        slotIndex: Int,
        hero: Hero,
        zone: ZoneType,
        savePayloadJson: String
    ): Result<Unit> {
        val uid = requireUserId()
        val saveId = "slot_$slotIndex"
        val docRef = db.collection("users").document(uid).collection("saves").document(saveId)

        return try {
            val existingSnapshot = docRef.get().await()
            val isCreate = !existingSnapshot.exists()

            val data = mutableMapOf<String, Any>(
                "saveId" to saveId,
                "userId" to uid,
                "heroName" to hero.name,
                "heroClass" to hero.heroClass.name,
                "level" to hero.level,
                "zone" to zone.name,
                "currentHp" to hero.currentHp,
                "maxHp" to hero.maxHp,
                "currentMp" to hero.currentMp,
                "maxMp" to hero.maxMp,
                "exp" to hero.exp,
                "gold" to hero.gold,
                "gems" to hero.gems,
                "savePayloadJson" to savePayloadJson,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (isCreate) {
                data["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(data).await()
            } else {
                docRef.update(data).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun loadHeroFromCloud(slotIndex: Int): Result<UserSaveDoc?> {
        val uid = requireUserId()
        val saveId = "slot_$slotIndex"
        val docRef = db.collection("users").document(uid).collection("saves").document(saveId)

        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                Result.success(snapshot.toObject(UserSaveDoc::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    // Leaderboard
    fun observeLeaderboard(): Flow<List<LeaderboardDoc>> = flow {
        val path = "leaderboard"
        emitAll(
            db.collection("leaderboard")
                .orderBy("level", Query.Direction.DESCENDING)
                .limit(25)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(LeaderboardDoc::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun publishToLeaderboard(
        hero: Hero,
        bossTrophies: Int,
        dungeonClears: Int
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("leaderboard").document(uid)

        return try {
            val existing = docRef.get().await()
            val isCreate = !existing.exists()

            val data = mutableMapOf<String, Any>(
                "userId" to uid,
                "heroName" to hero.name,
                "heroClass" to hero.heroClass.name,
                "level" to hero.level,
                "bossTrophies" to bossTrophies,
                "dungeonClears" to dungeonClears,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (isCreate) {
                data["createdAt"] = FieldValue.serverTimestamp()
                docRef.set(data).await()
            } else {
                docRef.update(data).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
