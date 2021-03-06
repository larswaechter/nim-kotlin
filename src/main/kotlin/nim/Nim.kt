package nim

/**
 *  Implementation of Nim with Minimax-Algorithm
 *
 * @property board Nim board
 * @property history history of boards
 * @property currentPlayer current player
 */
class Nim(
        override val board: IntArray,
        override val history: List<IntArray> = listOf(board),
        override val currentPlayer: Int = 1) : NimGame, Minimax<NimGame, Move> {

    /**
     * Do move and return new game
     *
     * @param [move] move to perform
     * @return new game with applied move
     */
    override fun move(move: Move): NimGame {
        assert(!this.isGameOver())
        assert(move.row >= 0 && move.row < this.board.size && move.amount > 0 && move.amount <= this.board[move.row])

        val board = this.board.clone()
        board[move.row] -= move.amount

        return Nim(board, this.history.plus(board), -this.currentPlayer)
    }

    /**
     * Undo a number of moves
     *
     * @param [number] number of moves to undo
     * @return new game with undone moves
     */
    override fun undoMove(number: Int): NimGame {
        assert(number < this.history.size)
        val nextPlayer: Int = if (number % 2 == 0) this.currentPlayer else -this.currentPlayer
        return Nim(this.history[this.history.size - 1 - number], this.history.subList(0, this.history.size - number), nextPlayer)
    }

    /**
     * Get best possible move
     * If player can not win we return a random move
     *
     * @return best possible or random move
     */
    override fun bestMove(): Move = this.recBestMove()

    /**
     * See bestMove()
     *
     * @param [possibleMoves] list of possible moves
     * @param [winMoves] list of moves that guarantee a win
     * @return best possible or random move
     */
    private fun recBestMove(possibleMoves: List<Move> = this.getPossibleMoves(), winMoves: List<Move> = listOf()): Move {
        // Recursion anchor -> Return bestMove or a random one
        if (possibleMoves.isEmpty()) return if (winMoves.isNotEmpty()) winMoves.random() else this.getRandomMove()

        // Evaluate move
        val newGame = this.move(possibleMoves.first())
        val score = this.minimax(game = newGame as Minimax<NimGame, Move>)

        // The move is a good one if the following player's move is a bad one
        if (!score.third) return this.recBestMove(possibleMoves.drop(1), winMoves.plus(possibleMoves.first()))

        // It was a bad move
        return this.recBestMove(possibleMoves.drop(1), winMoves)
    }

    /**
     * Get a list of all possible moves
     *
     * @return possible moves
     */
    override fun getPossibleMoves(): List<Move> {
        val possibleMoves: MutableList<Move> = mutableListOf()

        for (rowIdx in this.board.indices) {
            for (amount in this.board[rowIdx] downTo 1) {
                possibleMoves.add(Move(rowIdx, amount))
            }
        }

        return possibleMoves.toList()
    }

    /**
     * Evaluate current game board for Minimax algorithm
     *
     * @param [depth] current tree depth
     * @return score of board
     */
    override fun evaluate(depth: Int): Int = -this.currentPlayer * (depth + 1)

    /**
     * Check if no more moves are possible
     *
     * @return is game over
     */
    override fun isGameOver(): Boolean = this.board.none { n -> n > 0 }

    override fun toString(): String {
        var s = ""
        this.board.forEachIndexed { index, i -> s += "\n (${index + 1})\t" + "I ".repeat(i) }
        return s
    }
}