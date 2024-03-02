package com.jacobstechnologies.equal

class Puzzle(puzzleData: String) {
    private val charArray = puzzleData.toCharArray()
    lateinit var solution: String

    var operators = List(charArray.size) { i -> (OperatorCardData(
        value = charArray[i],
        key = i.toString()))
    }

    fun shuffle(){
        operators = operators.shuffled()
    }

    fun showSolution() : List<OperatorCardData> {
        val solutionCharArray = solution.toCharArray()
        operators = List(solutionCharArray.size) { i -> (OperatorCardData(
            value = solution[i],
            key = i.toString()))
        }
        return operators
    }
}

data class PuzzleSettings(val title: String, val numberCount: Int, val maxDigit: Int, val possibleOperators: CharArray) {
    override fun equals(other : Any?) : Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PuzzleSettings

        if (!possibleOperators.contentEquals(other.possibleOperators)) return false

        return true
    }
    override fun hashCode() : Int {
        return possibleOperators.contentHashCode()
    }
}