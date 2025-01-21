package com.nemislimus.crossvszero.ui.models

import com.nemislimus.crossvszero.domain.models.Player

sealed interface PlayersState {

    data object SelectDisable : PlayersState

    data class SelectEnable(
        val players: List<Player>,
    ) : PlayersState
}