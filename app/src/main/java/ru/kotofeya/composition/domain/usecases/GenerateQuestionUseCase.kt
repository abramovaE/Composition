package ru.kotofeya.composition.domain.usecases

import ru.kotofeya.composition.domain.entity.Question
import ru.kotofeya.composition.domain.repository.GameRepository

class GenerateQuestionUseCase(private val repository: GameRepository) {
    operator fun invoke(maxSumValue: Int): Question{
        return repository.generateQuestion(maxSumValue, COUNT_OF_OPTIONS)
    }

    private companion object{
        const val COUNT_OF_OPTIONS = 6
    }
}