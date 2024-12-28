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

    private lateinit var players: List<Player>

    private var playersState: MutableLiveData<PlayersState> = MutableLiveData()
    fun getPlayersState(): LiveData<PlayersState> = playersState

    init {
        viewModelScope.launch {
            players = playersRepository.getAllPlayers()
        }
    }

}