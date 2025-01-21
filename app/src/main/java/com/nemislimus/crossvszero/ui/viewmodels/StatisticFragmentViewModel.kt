package com.nemislimus.crossvszero.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemislimus.crossvszero.domain.api.PlayersRepository
import com.nemislimus.crossvszero.domain.models.Player
import com.nemislimus.crossvszero.ui.models.StatisticState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class StatisticFragmentViewModel(
    private val playersRepository: PlayersRepository,
) : ViewModel() {

    init {
        setStatisticState()
    }

    private var statState: MutableLiveData<StatisticState> = MutableLiveData()
    fun getStatisticState(): LiveData<StatisticState> = statState

    private fun setStatisticState() {
        viewModelScope.launch {
            val playerJob = async {
                getPlayers()
            }
            val players = playerJob.await()
            setState(players)
        }
    }

    suspend fun getPlayers(): List<Player> = playersRepository.getAllPlayers()

    fun setState(players: List<Player>) {
        if (players.isNotEmpty()) {
            statState.postValue(StatisticState.ShowStatistic(players))
        } else {
            statState.postValue(StatisticState.NoStatistic)
        }
    }

    suspend fun cleanupPlayerStat(player: Player) {
        val updatedPlayer = player.copy(victories = 0, defeats = 0)
        playersRepository.updatePlayerInDatabase(updatedPlayer)
    }

    suspend fun deletePlayer(playerName: String) {
        playersRepository.deletePlayerByName(playerName)
    }

}