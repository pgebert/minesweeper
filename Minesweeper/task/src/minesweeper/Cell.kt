package minesweeper

/**
 * Cell represents a field in the minesweeper game
 *
 * @property isMine whether this cell contains a mine
 * @property isMarked whether the player already marked this cell as mine
 * @property isExplored whether the player revealed this cell
 * @property hint the indicator how many mines are around this cell
 * @constructor Create empty Cell
 */
data class Cell(
    var isMine: Boolean = false,
    var isMarked: Boolean = false,
    var isExplored: Boolean = false,
    var hint: Int = 0
)
