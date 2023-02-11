package com.example.android.unscramble.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameDataSource @Inject constructor(
    @ApplicationContext context: Context // hilt가 자동으로 주입해줌.
){
        private val dataStore = context.gameDataStore

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