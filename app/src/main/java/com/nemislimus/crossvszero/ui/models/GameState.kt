package com.nemislimus.crossvszero.ui.models

import com.nemislimus.crossvszero.domain.models.GameCell

sealed interface GameState {

    data object NewGame : GameState

    data class GameInProcess(
        val cells: List<GameCell>,
        val winCellsIndexes: List<Int>,
        val weak: List<Int>,
        val zeroTurn: Boolean = true
    ) : GameState

    data class GotWinner(
        val cells: List<GameCell>,
        val winCellsIndexes: List<Int>,
        val zeroTurn: Boolean = true
    ) : GameState

    data class NoWinner(
        val cells: List<GameCell>,
        val winCellsIndexes: List<Int>,
        val zeroTurn: Boolean = true
    ) : GameState
}