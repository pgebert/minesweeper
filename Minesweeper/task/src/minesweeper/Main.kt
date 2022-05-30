package minesweeper

import kotlin.random.Random


class Explosion : Exception()
class IllegalCommand(message: String) : Exception(message)


const val FIELD_SIZE = 9

/**
 * Game loop and entry point.
 *
 */
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

/**
 * Creates a new game.
 *
 * @return new minesweeper game instance
 */
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