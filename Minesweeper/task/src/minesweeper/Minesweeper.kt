package minesweeper

const val MINE = "X"
const val UNEXPLORED = "."
const val MARKER = "*"
const val FREE = "/"

/**
 * Minesweeper represents the mineweeper game.
 *
 * @constructor Create a new game.
 *
 * @param size number of cells for with / height
 */
class Minesweeper(size: Int) {

    /**
     * Minesweeper field.
     */
    private val field = MutableList(size) {
        MutableList(size) {
            Cell()
        }
    }

    /**
     * Set a mine on a cell.
     *
     * @param row row
     * @param col column
     */
    fun setIsMine(row: Int, col: Int) {
        field[row][col].isMine = true
    }

    /**
     * Whether this cell has mines around or not.
     *
     * @param row row
     * @param col column
     * @return whether this cell has mines around or not
     */
    private fun hasMinesAround(row: Int, col: Int): Boolean {
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

    /**
     * Toggle mine marker of the cell.
     *
     * @param row row
     * @param col column
     */
    private fun toggleIsMarked(row: Int, col: Int) {

        if (field[row][col].hint > 0) throw IllegalCommand("Field $row $col is hint - can not mark!")

        field[row][col].isMarked = !field[row][col].isMarked
    }

    /**
     * Get number of mines on the mineweeper field.
     *
     * @return number of mines on the field
     */
    fun getNumberOfMines() = field.sumOf { row -> row.count { it.isMine } }

    /**
     * Create mine hints for the minesweeper field.
     *
     */
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

    /**
     * Explore cells recursively around the current cell.
     *
     * @param row row
     * @param col column
     */
    private fun exploreCellsRecursively(row: Int, col: Int) {

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

    /**
     * Explore a cell.
     *
     * @param row row
     * @param col column
     */
    private fun exploreCell(row: Int, col: Int) {

        if (field[row][col].isMine) throw Explosion()
        if (field[row][col].isExplored || field[row][col].hint > 0) throw IllegalCommand("Field $row $col already explored!")

        field[row][col].isExplored = true

        exploreCellsRecursively(row, col)
    }

    /**
     * Handle user command
     *
     */
    fun handleUserCommand() {
        while (true) {

            try {

                println("Set/unset mines marks or claim a cell as free:")
                val input = readln().split(" ")

                assert(input.size == 3)

                val col = input[0].toInt() - 1
                val row = input[1].toInt() - 1
                val mode = input[2].uppercase()

                when (InputMode.valueOf(mode)) {
                    InputMode.MINE -> toggleIsMarked(row, col)
                    InputMode.FREE -> exploreCell(row, col)
                }

                break

            } catch (e: IllegalCommand) {
                print(e)
            }

        }
    }

    /**
     * Whether all mines on the field are marked correctly by the player.
     *
     *  @return whether all mines are marked correctly
     */
    fun isMarkedCorrectly() = field.flatMap { row -> row.map { it.isMine == it.isMarked } }.all { it }

    /**
     * Print current state of the minesweeper field.
     *
     * @param reveal whether to reveal unexplored cells or not
     */
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