package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nemislimus.crossvszero.domain.api.ClassicGameRepository
import com.nemislimus.crossvszero.ui.models.GameState

class GameFragmentViewModel(
    private val repository: ClassicGameRepository
) : ViewModel() {

    private var gameState = MutableLiveData<GameState>(GameState.NewGame)
    fun getGameState(): LiveData<GameState> = gameState


    private var isZeroTurn = MutableLiveData<Boolean>()
    fun isZeroTurn(): LiveData<Boolean> = isZeroTurn

    init {
        whoIsFirst()
    }

    private fun setGameState(state: GameState) {
        gameState.postValue(state)
    }

    private fun winCheck(): Boolean {
        return if (repository.winCheck()) {
            setGameState(
                GameState.GotWinner(
                    repository.getPlayerCells(),
                    repository.getWinCellsIndexes(),
                    repository.isZeroTurn()
                )
            )
            true
        } else false
    }

    private fun checkFieldFilling(): Boolean {
        return if (repository.checkFieldFilling()) {
            setGameState(
                GameState.NoWinner(
                    repository.getPlayerCells(),
                    repository.getWinCellsIndexes(),
                    repository.isZeroTurn()
                )
            )
            true
        } else false
    }

    private fun switchPlayer() {
        repository.switchPlayer()
        isZeroTurn.postValue(repository.isZeroTurn())
    }

    fun clickOnCell(index: Int) {
        repository.setFieldCellValue(index)
        if (winCheck()) return
        if (checkFieldFilling()) return
        setGameState(
            GameState.GameInProcess(
                repository.getPlayerCells(),
                repository.getWinCellsIndexes(),
                repository.isZeroTurn()
            )
        )
        switchPlayer()
    }

    fun resetFieldOnButtonClick() {
        repository.resetField()
        setGameState(GameState.NewGame)
    }

    fun resetFieldOnExit() = repository.resetField()

    fun whoIsFirst() {
        isZeroTurn.postValue(repository.whoIsFirst())
    }

}