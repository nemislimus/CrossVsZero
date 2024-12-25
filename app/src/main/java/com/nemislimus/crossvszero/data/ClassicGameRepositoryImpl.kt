package com.nemislimus.crossvszero.data

import com.nemislimus.crossvszero.domain.api.ClassicGameRepository
import com.nemislimus.crossvszero.domain.models.GameCell

class ClassicGameRepositoryImpl : ClassicGameRepository {
    private val gameField: MutableList<GameCell> = MutableList(FIELD_CELLS_NUMBER) { index -> GameCell(index) }
    private var playerCells: List<GameCell> = listOf()
    private var winCellsIndexes: List<Int> = listOf()
    private var isZeroTurn: Boolean = false

    override fun setFieldCellValue(cellIndex: Int) {
        if (isZeroTurn) {
            gameField[cellIndex] = GameCell(cellIndex, GameCell.ZERO_CELL)
        } else {
            gameField[cellIndex] = GameCell(cellIndex, GameCell.CROSS_CELL)
        }

        val playerCellsIndexList = getChosenCells().map { cell -> cell.index }
        updateCellsCountdown(playerCellsIndexList)
    }

    override fun winCheck(): Boolean {
        playerCells = getChosenCells()
        return checkWinMarkers(playerCells)
    }

    override fun getPlayerCells(): List<GameCell> = playerCells

    override fun getWinCellsIndexes(): List<Int> = winCellsIndexes

    override fun getWeakElementIndex(): Int {
        val anotherPlayerCells = getChosenCells(!isZeroTurn)
        val weakElementList = anotherPlayerCells.filter { cell -> cell.countdown == WEAK_CELL_MARKER }
        return if (weakElementList.isNotEmpty()) {
            val weakElementIndex = weakElementList[0].index
            gameField[weakElementIndex] = GameCell(weakElementIndex, GameCell.EMPTY_CELL)
            weakElementIndex
        } else {
            NO_WEAK_INDEX
        }
    }

    override fun checkFieldFilling(): Boolean {
        val fieldValuesList = gameField.map { it.value }
        val indexOfEmpty = fieldValuesList.indexOfFirst { value -> value == GameCell.EMPTY_CELL }
        return indexOfEmpty == -1
    }

    override fun resetField() {
        val clearField = MutableList(FIELD_CELLS_NUMBER) { index -> GameCell(index) }
        gameField.clear()
        gameField.addAll(clearField)
        playerCells = listOf()
        winCellsIndexes = listOf()
    }

    override fun whoIsFirst(): Boolean {
        val random = (0..1).random()
        isZeroTurn = random == 0
        return isZeroTurn
    }

    override fun isZeroTurn(): Boolean = isZeroTurn

    override fun switchPlayer() {
        isZeroTurn = !isZeroTurn
    }

    private fun getChosenCells(zeroTurn: Boolean = isZeroTurn): List<GameCell> {
        return if (zeroTurn) {
            gameField.filter { cell -> cell.value == GameCell.ZERO_CELL }
        } else {
            gameField.filter { cell -> cell.value == GameCell.CROSS_CELL }
        }
    }

    private fun checkWinMarkers(chosenCells: List<GameCell>): Boolean {
        val cellsIndexSet = chosenCells.map { it.index }.toSet()

        WIN_MARKERS.forEach { marker ->
            if (cellsIndexSet.containsAll(marker)) {
                winCellsIndexes = marker.toList()
                return true
            }
        }
        return false
    }

    private fun updateCellsCountdown(playerCellsIndexList: List<Int>) {
        gameField.forEachIndexed { index, gameCell ->
            if (playerCellsIndexList.contains(index)) {
                val newCount = gameCell.countdown + 1
                gameField[index] = GameCell(
                    index,
                    gameCell.value,
                    newCount
                )
            }
        }
    }

    companion object {
        private val WIN_MARKERS = listOf(
            setOf(0,1,2),
            setOf(3,4,5),
            setOf(6,7,8),
            setOf(0,3,6),
            setOf(1,4,7),
            setOf(2,5,8),
            setOf(0,4,8),
            setOf(2,4,6),
        )

        const val FIELD_CELLS_NUMBER = 9
        const val WEAK_CELL_MARKER = 3
        private const val NO_WEAK_INDEX = -1
    }
}