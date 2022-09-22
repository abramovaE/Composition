package ru.kotofeya.composition.domain.repository

import ru.kotofeya.composition.domain.entity.GameSettings
import ru.kotofeya.composition.domain.entity.Level
import ru.kotofeya.composition.domain.entity.Question

interface GameRepository {
    fun generateQuestion(maxSumValue: Int,
                         countOfOptions: Int): Question
    fun getGameSettings(level: Level): GameSettings
}
