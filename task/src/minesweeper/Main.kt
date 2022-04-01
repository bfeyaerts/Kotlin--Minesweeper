package minesweeper

import kotlin.random.Random

const val ROWS = 9
const val COLUMNS = 9

const val MINE = 'X'
const val CELL_UNEXPLORED = '.'
const val CELL_MARKED_FREE = '/'
const val CELL_MARKED_MINE = '*'

val underlyingField = MutableList(ROWS) {
    MutableList(COLUMNS) { '0' }
}
val visibleField = MutableList(ROWS) {
    MutableList(COLUMNS) { CELL_UNEXPLORED }
}

fun main() {
    println("How many mines do you want on the field?")
    val numberOfMines = readLine()!!.toInt()
    placeMines(numberOfMines)

    while (true) {
        printField(visibleField)

        println("Set/unset mines marks or claim a cell as free:")
        val (c, r, mark) = readLine()!!.split(" ")
        val row = r.toInt() - 1
        val column = c.toInt() - 1

        when (mark) {
            "free" -> {
                if (underlyingField[row][column] == MINE) {
                    // Loose
                    underlyingField.forEachIndexed { rowIndex, underlyingRow ->
                        underlyingRow.forEachIndexed { columnIndex, underlyingCell ->
                            if (underlyingCell == MINE) {
                                visibleField[rowIndex][columnIndex] = MINE
                            }
                        }
                    }

                    printField(visibleField)

                    println("You stepped on a mine and failed!")
                    break
                } else {
                    reveal(row, column)
                }
            }
            "mine" -> {
                if (visibleField[row][column] == CELL_UNEXPLORED) {
                    visibleField[row][column] = CELL_MARKED_MINE
                } else if (visibleField[row][column] == CELL_MARKED_MINE) {
                    visibleField[row][column] = CELL_UNEXPLORED
                }
            }
        }

        if (solved()) {
            printField(visibleField)
            println("Congratulations! You found all the mines!")
            break
        }
    }
}

fun increase(row: MutableList<Char>?, column: Int) {
    row?.let {
        if (it[column].isDigit()) {
            it[column] = it[column] + 1
        }
    }
}

fun placeMines(numberOfMines: Int) {
    for (i in 1..numberOfMines) {
        while (true) {
            val position = Random.nextInt(ROWS * COLUMNS)
            val row = position / COLUMNS
            val column = position % COLUMNS

            val currentRow = underlyingField[row]

            if (currentRow[column] != MINE) {
                val previousRow = if (row > 0) underlyingField[row - 1] else null
                val nextRow = if (row < ROWS - 1) underlyingField[row + 1] else null

                if (column > 0) {
                    increase(previousRow, column - 1)
                    increase(currentRow, column - 1)
                    increase(nextRow, column - 1)
                }
                increase(previousRow, column)
                currentRow[column] = MINE
                increase(nextRow, column)
                if (column < COLUMNS - 1) {
                    increase(previousRow, column + 1)
                    increase(currentRow, column + 1)
                    increase(nextRow, column + 1)
                }
                break
            }
        }
    }
}

fun solved(): Boolean {
    for (r in underlyingField.indices) {
        val underlyingRow = underlyingField[r]
        val visibleRow = visibleField[r]
        for (c in underlyingRow.indices) {
            val underlyingCell = underlyingRow[c]
            val visibleCell = visibleRow[c]

            if (underlyingCell != '0' && underlyingCell.isDigit()) {
                continue
            } else if (underlyingCell == '0' && visibleCell == CELL_MARKED_MINE) {
                return false
            } else if (underlyingCell == MINE && visibleCell == CELL_UNEXPLORED) {
                return false
            }
        }
    }
    return true
}

fun reveal(row: Int, column: Int, visitedCells: MutableSet<Pair<Int, Int>> = mutableSetOf()) {
    if (visitedCells.contains(Pair(row, column)))
        return
    if (row < 0 || column < 0)
        return
    if (row >= ROWS || column >= COLUMNS)
        return
    if (underlyingField[row][column] == MINE)
        return

    if (underlyingField[row][column] == '0') {
        visibleField[row][column] = CELL_MARKED_FREE
        visitedCells.add(Pair(row, column))
        reveal(row - 1, column - 1, visitedCells)
        reveal(row - 1, column, visitedCells)
        reveal(row - 1, column + 1, visitedCells)
        reveal(row, column - 1, visitedCells)
        reveal(row, column + 1, visitedCells)
        reveal(row + 1, column - 1, visitedCells)
        reveal(row + 1, column, visitedCells)
        reveal(row + 1, column + 1, visitedCells)
    } else if (underlyingField[row][column].isDigit()) {
        visibleField[row][column] = underlyingField[row][column]
        visitedCells.add(Pair(row, column))
    }
}

fun printField(field: List<List<Char>>) {
    println()
    // Header
    print(" │")
    for (c in 1..COLUMNS)
        print(c)
    println("│")

    print("—|")
    repeat(COLUMNS) {
        print("—")
    }
    println("│")

    field.forEachIndexed { rowIndex, row ->
        print("${rowIndex + 1}|")
        print(row.joinToString(""))
        println("│")
    }

    print("—│")
    repeat(COLUMNS) {
        print("—")
    }
    println("│")
}