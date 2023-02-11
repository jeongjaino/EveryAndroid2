package com.example.android.unscramble.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val dataSource: GameDataSource
): ViewModel() {

    val highScore: Flow<Int> = dataSource.gamePreferencesFlow
        .map { preferences -> preferences.highScore } // TODO

    suspend fun updateScore(score: Int){
        dataSource.updateHighScore(score)
    }
}
