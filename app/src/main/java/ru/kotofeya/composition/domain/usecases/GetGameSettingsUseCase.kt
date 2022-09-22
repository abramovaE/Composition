package ru.kotofeya.composition.domain.usecases

import ru.kotofeya.composition.domain.entity.GameSettings
import ru.kotofeya.composition.domain.entity.Level
import ru.kotofeya.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(private val repository: GameRepository) {
    operator fun invoke(level: Level) : GameSettings{
        return repository.getGameSettings(level)
    }
}