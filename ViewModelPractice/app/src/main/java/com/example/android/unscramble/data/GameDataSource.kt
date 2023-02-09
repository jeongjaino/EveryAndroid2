package com.example.android.unscramble.data

import android.app.Application
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameDataSource (
    application : Application
){
        private val dataStore = application.gameDataStore

        val gamePreferencesFlow : Flow<GamePreferences> = dataStore.data.map{ preferences ->
                val highScore = preferences[PreferencesKeys.HIGH_SCORE] ?: 0
                    GamePreferences(highScore = highScore)
         }

        suspend fun updateHighScore(score: Int){
                dataStore.edit{ preferences ->
                        val currentHighScore = preferences[PreferencesKeys.HIGH_SCORE] ?: 0
                        if (currentHighScore < score){
                                preferences[PreferencesKeys.HIGH_SCORE] = score
                        }
                }
        }
}