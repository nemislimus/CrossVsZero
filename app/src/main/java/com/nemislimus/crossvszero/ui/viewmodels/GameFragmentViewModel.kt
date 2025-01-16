package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemislimus.crossvszero.domain.api.ClassicGameRepository
import com.nemislimus.crossvszero.domain.api.PlayersRepository
import com.nemislimus.crossvszero.domain.models.Player
import com.nemislimus.crossvszero.ui.models.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameFragmentViewModel(
    private val gameRepository: ClassicGameRepository,
    private val playersRepository: PlayersRepository,
    private val nameOfCross: String?,
    private val nameOfZero: String?,
) : ViewModel() {

    private var playerX: Player? = null
    private var playerO: Player? = null

    var weakElementIndex: Int? = null

    private var gameState = MutableLiveData<GameState>(GameState.NewGame)
    fun getGameState(): LiveData<GameState> = gameState


    private var isZeroTurn = MutableLiveData<Boolean>()
    fun isZeroTurn(): LiveData<Boolean> = isZeroTurn

    init {
        viewModelScope.launch {
            val playersJob = launch(Dispatchers.IO) {
                setPlayers(nameOfCross, nameOfZero)
            }
            playersJob.join()
            whoIsFirst()
        }
    }

    private fun setGameState(state: GameState) {
        gameState.postValue(state)
    }

    private fun winCheck(): Boolean {
        return if (gameRepository.winCheck()) {
            setGameState(
                GameState.GotWinner(
                    gameRepository.getPlayerCells(),
                    gameRepository.getWinCellsIndexes(),
                    gameRepository.isZeroTurn()
                )
            )
            true
        } else false
    }

    private fun checkFieldFilling(): Boolean {
        return if (gameRepository.checkFieldFilling()) {
            setGameState(
                GameState.NoWinner(
                    gameRepository.getPlayerCells(),
                    gameRepository.getWinCellsIndexes(),
                    gameRepository.isZeroTurn()
                )
            )
            true
        } else false
    }

    private fun switchPlayer() {
        gameRepository.switchPlayer()
        isZeroTurn.postValue(gameRepository.isZeroTurn())
    }

    fun clickOnCell(index: Int) {
        gameRepository.setFieldCellValue(index)
        if (winCheck()) return
        if (checkFieldFilling()) return
        val indexOfWeakElement = gameRepository.getWeakElementIndex()

        setGameState(
            GameState.GameInProcess(
                gameRepository.getPlayerCells(),
                gameRepository.getWinCellsIndexes(),
                indexOfWeakElement,
                gameRepository.isZeroTurn()
            )
        )
        switchPlayer()
        if (indexOfWeakElement != -1) weakElementIndex = indexOfWeakElement
    }

    fun resetFieldOnButtonClick() {
        weakElementIndex = null
        gameRepository.resetField()
        setGameState(GameState.NewGame)
    }

    fun resetFieldOnExit() {
        weakElementIndex = null
        gameRepository.resetField()
    }

    private suspend fun setPlayers(xName: String?, oName: String?) {
        xName?.let { playerX = playersRepository.getPlayerByName(it) }
        oName?.let { playerO = playersRepository.getPlayerByName(it) }
    }

    fun whoIsFirst() {
        isZeroTurn.postValue(gameRepository.whoIsFirst())
    }

}