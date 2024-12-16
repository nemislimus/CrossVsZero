package com.nemislimus.crossvszero.ui.models

sealed interface GameState {

    data object NewGame: GameState

    data class GameInProcess(val gameField: Array<GameCell>): GameState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameInProcess

            return gameField.contentEquals(other.gameField)
        }

        override fun hashCode(): Int {
            return gameField.contentHashCode()
        }
    }

    data class FinishGame(val finishField: Array<GameCell>): GameState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameInProcess

            return finishField.contentEquals(other.gameField)
        }

        override fun hashCode(): Int {
            return finishField.contentHashCode()
        }
    }
}