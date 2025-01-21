package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemislimus.crossvszero.domain.api.PlayersRepository
import com.nemislimus.crossvszero.domain.models.Player
import com.nemislimus.crossvszero.ui.models.PlayersState
import kotlinx.coroutines.launch

class PlayersFragmentViewModel(
    private val playersRepository: PlayersRepository,
) : ViewModel() {

    private var playersState: MutableLiveData<PlayersState> = MutableLiveData(PlayersState.SelectDisable)
    fun getPlayersState(): LiveData<PlayersState> = playersState

    suspend fun getPlayers(): List<Player> = playersRepository.getAllPlayers()

    suspend fun savePlayer(playerName: String) {
        val player = Player(
            id = 0,
            name = playerName,
            victories = 0,
            defeats = 0,
        )
        playersRepository.addPlayer(player)
    }

    suspend fun managePlayerSelectionOption(enable: Boolean) {
        if (enable) {
            viewModelScope.launch {
                var players: List<Player> = emptyList()
                val getPlayersJob = launch {
                    players = getPlayers()
                }
                getPlayersJob.join()
                playersState.postValue(PlayersState.SelectEnable(players))
            }
        } else {
            playersState.postValue(PlayersState.SelectDisable)
        }
    }

}