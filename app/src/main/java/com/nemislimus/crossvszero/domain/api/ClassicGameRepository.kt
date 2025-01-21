package com.nemislimus.crossvszero.domain.api

import com.nemislimus.crossvszero.domain.models.GameCell

interface ClassicGameRepository {
    fun whoIsFirst(): Boolean
    fun isZeroTurn(): Boolean
    fun setFieldCellValue(cellIndex: Int)
    fun winCheck(): Boolean
    fun getPlayerCells(): List<GameCell>
    fun getWinCellsIndexes(): List<Int>
    fun getWeakElementIndex(): Int
    fun checkFieldFilling(): Boolean
    fun resetField()
    fun switchPlayer()
}