package com.nemislimus.crossvszero.domain.api

import com.nemislimus.crossvszero.ui.models.GameCell

interface ClassicGameRepository {
    fun whoIsFirst(): Boolean
    fun isZeroTurn(): Boolean
    fun setFieldCellValue(index: Int, zeroTurn: Boolean)
    fun winCheck(zeroTurn: Boolean): Boolean
    fun getPlayerCells(): List<GameCell>
    fun getWinCellsIndexes(): List<Int>
    fun checkFieldFilling(zeroTurn: Boolean): Boolean
    fun resetField()
    fun switchPlayer()
}