package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nemislimus.crossvszero.domain.api.ClassicGameInteractor
import com.nemislimus.crossvszero.ui.models.GameCell
import com.nemislimus.crossvszero.ui.models.GameState

class GameFragmentViewModel(
    private val classicInteractor: ClassicGameInteractor
) : ViewModel() {

    private val gameField: MutableList<GameCell> = MutableList(9) { index -> GameCell(index) }
    private var playerCells: List<GameCell> = listOf()
    private var winCellsIndexes: List<Int> = listOf()
    private var turnCount: Int = 0

    private var gameState = MutableLiveData<GameState>(GameState.NewGame)
    fun getGameState(): LiveData<GameState> = gameState


    private var isZeroTurn = MutableLiveData(true)
    fun isZeroTurn(): LiveData<Boolean> = isZeroTurn

    init {
        whoIsFirst()
    }

    private fun switchPlayer() {
        var newValue = true
        isZeroTurn.value?.let { newValue = !it }
        isZeroTurn.postValue(newValue)
    }

    private fun setGameState(state: GameState) {
        gameState.postValue(state)
    }

    private fun setFieldCellValue(index: Int, zeroTurn: Boolean) {
        if (zeroTurn) {
            gameField[index] = GameCell(index, GameCell.ZERO_CELL)
        } else {
            gameField[index] = GameCell(index, GameCell.CROSS_CELL)
        }
    }

    private fun winCheck(zeroTurn: Boolean): Boolean {
        playerCells = getChosenCells(zeroTurn)
        return if (checkWinMarkers(playerCells)) {
            setGameState(GameState.GotWinner(playerCells, winCellsIndexes, zeroTurn))
            true
        } else false
    }

    private fun getChosenCells(zeroTurn: Boolean): List<GameCell> {
        return if (zeroTurn) {
            gameField.filter { cell -> cell.value == GameCell.ZERO_CELL }
        } else {
            gameField.filter { cell -> cell.value == GameCell.CROSS_CELL }
        }
    }

    private fun checkWinMarkers(chosenCells: List<GameCell>): Boolean {
        val cellsIndexSet = chosenCells.map { it.index }.toSet()

        WIN_MARKERS.forEach { marker ->
            if (cellsIndexSet.containsAll(marker)) {
                winCellsIndexes = marker.toList()
                return true
            }
        }
        return false
    }

    private fun checkFieldFilling(zeroTurn: Boolean): Boolean {
        val fieldValuesList = gameField.map { it.value }
        val indexOfEmpty = fieldValuesList.indexOfFirst { value -> value == GameCell.EMPTY_CELL }
        return if (indexOfEmpty == -1) {
            setGameState(GameState.NoWinner(playerCells, winCellsIndexes, zeroTurn))
            true
        } else false
    }

    fun clickOnCell(index: Int) {
        val isZeroTurn = isZeroTurn.value
        isZeroTurn?.let { zeroTurn ->
            setFieldCellValue(index, zeroTurn)
            if (winCheck(zeroTurn)) return
            if (checkFieldFilling(zeroTurn)) return
            setGameState(GameState.GameInProcess(playerCells, winCellsIndexes, zeroTurn))
            switchPlayer()
        }
    }

    fun resetField() {
        val clearField = gameField.map { it.copy(index = it.index, value = GameCell.EMPTY_CELL) }
        gameField.clear()
        gameField.addAll(clearField)
        playerCells = listOf()
        winCellsIndexes = listOf()
        setGameState(GameState.NewGame)
    }

    fun whoIsFirst() {
        val random = (0..1).random()
        isZeroTurn.postValue(random == 0)
    }

    companion object {
        // List of win index sets
        val WIN_MARKERS = listOf(
            setOf(0,1,2),
            setOf(3,4,5),
            setOf(6,7,8),
            setOf(0,3,6),
            setOf(1,4,7),
            setOf(2,5,8),
            setOf(0,4,8),
            setOf(2,4,6),
        )
    }
}