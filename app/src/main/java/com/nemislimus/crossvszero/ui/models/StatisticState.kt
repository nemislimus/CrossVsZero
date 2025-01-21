package com.nemislimus.crossvszero.ui.models

import com.nemislimus.crossvszero.domain.models.Player

sealed interface StatisticState {

    data object NoStatistic : StatisticState

    data class ShowStatistic(
        val players: List<Player>,
    ) : StatisticState
}