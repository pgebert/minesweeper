package minesweeper

import kotlin.random.Random


class Explosion : Exception()
class IllegalCommand(message: String) : Exception(message)


const val FIELD_SIZE = 9

const val MINE = "X"
const val UNEXPLORED = "."
const val MARKER = "*"
const val FREE = "/"

enum class InputMode {
    MINE,
    FREE
}


fun main() {
    val minesweeper = createGame()
    minesweeper.printField()

    var steppedOnMine = false

    try {

        while (!minesweeper.isMarkedCorrectly() && !steppedOnMine) {
            minesweeper.handleUserCommand()
            minesweeper.createHints()
            minesweeper.printField()
        }

        println("Congratulations! You found all the mines!")

    } catch (e: Explosion) {
        minesweeper.printField(reveal = true)
        println("You stepped on a mine and failed!\n")
    }


}


data class Cell(
    var isMine: Boolean = false,
    var isMarked: Boolean = false,
    var isExplored: Boolean = false,
    var hint: Int = 0
)

class Minesweeper(size: Int) {

    private val field = MutableList(size) {
        MutableList(size) {
            Cell()
        }
    }

    fun setIsMine(row: Int, col: Int) {
        field[row][col].isMine = true
    }

    fun hasMinesAround(row: Int, col: Int): Boolean {
        for (rowOffset in -1..1) {
            for (colOffset in -1..1) {

                if (rowOffset == 0 && colOffset == 0) continue

                if (row + rowOffset in field.indices
                    && col + colOffset in field[row + rowOffset].indices
                    && field[row + rowOffset][col + colOffset].isMine
                ) {
                    return true
                }

            }
        }
        return false

    }

    fun toggleIsMarked(row: Int, col: Int) {

        if (field[row][col].hint > 0) throw IllegalCommand("Field $row $col is hint - can not mark!")

        field[row][col].isMarked = !field[row][col].isMarked
    }

    fun getNumberOfMines() = field.sumOf { row -> row.count { it.isMine } }

    fun createHints() {
        for (row in field.indices) {
            for (col in field[row].indices) {
                field[row][col].hint = 0
            }
        }


        for (row in field.indices) {
            for (col in field[row].indices) {

                if (field[row][col].isMine) continue

                var hasExploredAround = false

                for (rowOffset in -1..1) {
                    for (colOffset in -1..1) {

                        if (row + rowOffset in field.indices
                            && col + colOffset in field[row + rowOffset].indices
                            && field[row + rowOffset][col + colOffset].isMine
                        ) {
                            field[row][col].hint = field[row][col].hint + 1
                        }

                        if (row + rowOffset in field.indices
                            && col + colOffset in field[row + rowOffset].indices
                            && field[row + rowOffset][col + colOffset].isExplored
                        ) {
                            hasExploredAround = true
                        }
                    }
                }

                if (!hasExploredAround) {
                    field[row][col].hint = 0
                }

            }
        }
    }

    fun exploreCellsRecursively(row: Int, col: Int) {

        if (!field[row][col].isMine && !hasMinesAround(row, col)) {
            field[row][col].isExplored = true

            for (rowOffset in -1..1) {
                for (colOffset in -1..1) {

                    if (rowOffset == 0 && colOffset == 0) continue

                    if (row + rowOffset in field.indices
                        && col + colOffset in field[row + rowOffset].indices
                        && !field[row + rowOffset][col + colOffset].isExplored
                    ) {
                        exploreCellsRecursively(row + rowOffset, col + colOffset)
                    }

                }
            }
        }


    }

    fun exploreCell(row: Int, col: Int) {

        if (field[row][col].isMine) throw Explosion()
        if (field[row][col].isExplored || field[row][col].hint > 0) throw IllegalCommand("Field $row $col already explored!")

        field[row][col].isExplored = true

        exploreCellsRecursively(row, col)
    }

    fun handleUserCommand() {
        while (true) {

            try {

                println("Set/unset mines marks or claim a cell as free:")
                val input = readln().split(" ")

                assert(input.size == 3)

                val col = input[0].toInt() - 1
                val row = input[1].toInt() - 1
                val mode = InputMode.valueOf(input[2].uppercase())

                when (mode) {
                    InputMode.MINE -> toggleIsMarked(row, col)
                    InputMode.FREE -> exploreCell(row, col)
                }

                break

            } catch (e: IllegalCommand) {
                print(e)
            }

        }
    }

    fun isMarkedCorrectly() = field.flatMap { row -> row.map { it.isMine == it.isMarked } }.all { it }

    fun printField(reveal: Boolean = false) {

        println(" |${(1..FIELD_SIZE).joinToString("")}|")
        println("-|${List(FIELD_SIZE) { "-" }.joinToString("")}|")

        field.forEachIndexed { index, row ->
            println("${index + 1}|"
                    + row.joinToString("") { cell ->
                when {
                    cell.hint > 0 -> cell.hint.toString()
                    cell.isExplored -> FREE
                    cell.isMarked -> MARKER
                    cell.isMine && reveal -> MINE
                    else -> UNEXPLORED
                }
            }
                    + "|"
            )
        }

        println("-|${List(FIELD_SIZE) { "-" }.joinToString("")}|")
    }

}


fun createGame(): Minesweeper {
    val minesweeper = Minesweeper(FIELD_SIZE)

    println("How many mines do you want on the field?")
    val numberOfMines = readln().toInt()

    while (minesweeper.getNumberOfMines() < numberOfMines) {
        val row = Random.nextInt(FIELD_SIZE)
        val col = Random.nextInt(FIELD_SIZE)
        minesweeper.setIsMine(row, col)
    }

    return minesweeper
}