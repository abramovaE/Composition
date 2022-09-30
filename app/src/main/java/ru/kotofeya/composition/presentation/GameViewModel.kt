package ru.kotofeya.composition.presentation

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.kotofeya.composition.R
import ru.kotofeya.composition.data.GameRepositoryImpl
import ru.kotofeya.composition.domain.entity.GameResult
import ru.kotofeya.composition.domain.entity.GameSettings
import ru.kotofeya.composition.domain.entity.Level
import ru.kotofeya.composition.domain.entity.Question
import ru.kotofeya.composition.domain.usecases.GenerateQuestionUseCase
import ru.kotofeya.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(private val application: Application,
                    private val level: Level): ViewModel() {

    private val repository = GameRepositoryImpl
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private lateinit var gameSettings: GameSettings


    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val processAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCountOfRightAnswers = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = gameResult

    private var timer: CountDownTimer? = null
    private var countOfRightAnswers = 0
    private var countOfQuestions = 0

    init {
        startGame()
    }


    fun startGame(){
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int){
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress(){
        val percent = calculatePercentOfRightAnswers()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value = countOfRightAnswers >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSettings.minPercentOfRightAnswers
    }

    private fun calculatePercentOfRightAnswers(): Int{
        if(countOfQuestions == 0){
            return 0
        }
        return (countOfRightAnswers/countOfQuestions.toDouble() * 100).toInt()
    }

    private fun checkAnswer(number: Int){
        val rightAnswer = question.value?.rightAnswer
        if(number == rightAnswer){
            countOfRightAnswers++
        }
        countOfQuestions++
    }


    private fun getGameSettings(){
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer(){
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ){
            override fun onTick(p0: Long) {
                _formattedTime.value = formatTime(p0)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun generateQuestion(){
        val maxSumValue = gameSettings.maxSumValue
        val question = generateQuestionUseCase(maxSumValue)
        _question.value = question
    }

    private fun formatTime(millis: Long): String{
        val seconds = millis / MILLIS_IN_SECONDS
        val minutes = millis / SECONDS_IN_MINUTES
        val leftSeconds = seconds - minutes * SECONDS_IN_MINUTES
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame(){
        val gameResult = GameResult(
            winner = enoughCountOfRightAnswers.value == true
                    && enoughCountOfRightAnswers.value == true,
            countOfRightAnswers = countOfRightAnswers,
            countOfQuestions = countOfQuestions,
            gameSettings = gameSettings
        )
        _gameResult.value = gameResult
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object{
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60

    }

}