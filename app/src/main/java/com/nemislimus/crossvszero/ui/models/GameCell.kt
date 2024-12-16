package com.nemislimus.crossvszero.ui.models


data class GameCell(
    val index: Int,
    val value: Int = EMPTY_CELL,
    val countdown: Int = 0
) {
    companion object {
        const val EMPTY_CELL = 2
        const val ZERO_CELL = 0
        const val CROSS_CELL = 1
    }
}
