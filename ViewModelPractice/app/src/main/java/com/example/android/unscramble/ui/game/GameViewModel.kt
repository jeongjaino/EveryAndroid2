package com.example.android.unscramble.ui.game

import com.example.android.unscramble.data.GameRepository
import android.app.Application
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.util.*

class SavableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key : String,
    initialValue: T
){
    private val state: StateFlow<T> = savedStateHandle.getStateFlow(key, initialValue)
    var value : T
        get() = state.value
        set(value){
        savedStateHandle[key] = value
        }

    fun asStateFlow(): StateFlow<T> = state
}

fun <T> SavedStateHandle.getMutableStateFlow(key: String, initialValue: T): SavableMutableStateFlow<T> =
    SavableMutableStateFlow(this, key, initialValue)

class GameViewModel(
    private val stateHandler: SavedStateHandle,
    private val repository : GameRepository
): ViewModel() {

    private var wordsList: List<String> // 게임 사용 단어 목록 보유
        get() = stateHandler["wordsList"] ?: emptyList()
        set(value){
            stateHandler["wordsList"] = value
        }

    private var currentWord: String // 추측한 단어 상태
        get() = stateHandler["currentWord"] ?: ""
        set(value){
            val tempWord = value.toCharArray()
            do{
                tempWord.shuffle() // 글자 순서 변경
            } while(String(tempWord) == value)
            _currentScrambleWord.value = String(tempWord)
            _currentWordCount.value += 1
            wordsList = wordsList + (currentWord)

            stateHandler["currentWord"] = value
        }

    // saveStateHandler 를 사용한 score 값 저장
    private val _score = stateHandler.getMutableStateFlow("score", 0)
    val score: StateFlow<Int>
        get() = _score.asStateFlow()

    val highScore : StateFlow<Int> = repository.highScore.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0
    )

    private val _currentWordCount =stateHandler.getMutableStateFlow("currentWordCount", 0) // 현재 단어 번호
    val currentWordCount: StateFlow<Int>
        get() = _currentWordCount.asStateFlow()

    private val _currentScrambleWord = stateHandler.getMutableStateFlow("currentScrambledWord", "") // viewModel에서만 사용
    val currentScrambleWord : StateFlow<Spannable> = _currentScrambleWord
        .asStateFlow()
        .onSubscription {
            if(currentWord.isEmpty()) {
                nextWord()
            }
        }
        .map{ scrambleWord ->
            val spannable: Spannable = SpannableString(scrambleWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambleWord).build(),
                0,
                scrambleWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
        // map은 flow로 반환, stateIn()은 stateFlow로 반환
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SpannableString(""))

    fun nextWord(): Boolean{
        return if(currentWordCount.value < MAX_NO_OF_WORDS){
            var nextWord : String
            do{
                nextWord = allWordsList.random(Random(Calendar.getInstance().timeInMillis))
            } while(wordsList.contains(currentWord))
            currentWord = nextWord
            true
        }else false
    }

    // 맞추는 경우 점수를 높이는 함수
    private fun increaseScore(){
        _score.value += SCORE_INCREASE
        viewModelScope.launch {
            repository.updateScore(_score.value)
        }
    }

    // 검증 함수
    fun isUserWordCorrect(playerWord: String): Boolean{
        if(playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList = emptyList()
        nextWord()
    }
}

// viewModel을 생성하기 위해 custom한 요소들 생성자로 받아야함.
// Factory를 활용하여, savedStateHandle을 생성하고 관리하기 귀찮은 부분 해결 .
class GameViewModelFactory(
    private val application : Application,
    owner : SavedStateRegistryOwner,
    defaultArgs : Bundle? = null,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        // modelClass가 gameViewModel로 대입이 되었는가
        require(modelClass.isAssignableFrom(GameViewModel::class.java)){
            "Unknown ViewModel class"
        }

        @Suppress("UNCHECKED_CAST")
        return GameViewModel(
            stateHandler = handle,
            repository = GameRepository(application)
        ) as T // java와 kotlin의 nullable 차이
    }
}