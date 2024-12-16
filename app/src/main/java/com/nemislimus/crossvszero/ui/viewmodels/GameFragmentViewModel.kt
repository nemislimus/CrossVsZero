package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nemislimus.crossvszero.ui.models.GameCell
import com.nemislimus.crossvszero.ui.models.GameState

class GameFragmentViewModel : ViewModel() {

    private val gameField: Array<GameCell> = Array(9) { index -> GameCell(index) }
    private var turnCount: Int = 0

    private var gameState = MutableLiveData<GameState>(GameState.NewGame)
    fun getGameState(): LiveData<GameState> = gameState


    private var isZeroTurn = MutableLiveData(true)
    fun isZeroTurn(): LiveData<Boolean> = isZeroTurn

    init {
        whoIsFirst()
    }

    private fun whoIsFirst() {
        val random = (0..1).random()
        isZeroTurn.postValue(random == 0)
    }

    private fun switchPlayer() {
        var newValue = true
        isZeroTurn.value?.let { newValue = !it }
        isZeroTurn.postValue(newValue)
    }

    private fun setGameState(state: GameState = GameState.GameInProcess(gameField)) {
        gameState.postValue(state)
    }

    private fun setFieldCellValue(index: Int) {
        if (isZeroTurn.value == true) {
            gameField[index] = GameCell(index, GameCell.ZERO_CELL)
        } else {
            gameField[index] = GameCell(index, GameCell.CROSS_CELL)
        }
    }

    fun clickOnCell(index: Int) {
        setFieldCellValue(index)

        // win check
        // cell disappear check

        setGameState()
        switchPlayer()
    }
}