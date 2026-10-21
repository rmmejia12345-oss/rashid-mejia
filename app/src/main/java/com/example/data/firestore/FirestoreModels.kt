package com.example.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserSaveDoc(
    @DocumentId val id: String = "",
    val saveId: String = "",
    val userId: String = "",
    val heroName: String = "",
    val heroClass: String = "",
    val level: Int = 1,
    val zone: String = "",
    val currentHp: Int = 0,
    val maxHp: Int = 0,
    val currentMp: Int = 0,
    val maxMp: Int = 0,
    val exp: Int = 0,
    val gold: Int = 0,
    val gems: Int = 0,
    val savePayloadJson: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LeaderboardDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val heroName: String = "",
    val heroClass: String = "",
    val level: Int = 1,
    val bossTrophies: Int = 0,
    val dungeonClears: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
