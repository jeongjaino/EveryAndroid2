package com.example.android.unscramble.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.gameDataStore by preferencesDataStore(
    name = "GamePreferences"
)

object PreferencesKeys{
    val HIGH_SCORE = intPreferencesKey("high_score")
}

data class GamePreferences (
    val highScore : Int
)