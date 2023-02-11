package com.example.android.unscramble.data

import android.app.Application
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepository @Inject constructor(
    application: Application,
    private val dataSource: GameDataSource = GameDataSource(application)
): ViewModel() {

    val highScore: Flow<Int> = dataSource.gamePreferencesFlow
        .map { preferences -> preferences.highScore } // TODO

    suspend fun updateScore(score: Int){
        dataSource.updateHighScore(score)
    }
}
