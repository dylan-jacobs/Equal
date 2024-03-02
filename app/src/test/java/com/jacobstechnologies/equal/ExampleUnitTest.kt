package com.jacobstechnologies.equal

import android.graphics.Path.Op
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkCorrect(){
        val e = ExpressionBuilder("5%5").build()
        print(e.evaluate())

    }

    @Test
    fun go(){
        val numbers = (0 until 10).map { (0..9).random() }
        val possibleOperators = charArrayOf('+', '-', '*', '/', '%', '^')
        val operators = (0 until(10 - 1)).map { possibleOperators[(0 until 4).random()] }
        val sb = StringBuilder()
        numbers.forEach{
            sb.append(it.toString())
        }
        operators.forEach{
            sb.append(it.toString())
        }
        println(sb)
    }

    @Test
    fun getPuzzle(){
        var puzzle = Puzzle("")
        val sb = StringBuilder()
        var numbers = (0 until 4).map { (1..10).random() }
        var iterations = 0
        while (!checkIfValidPuzzle(puzzle)) {
            sb.clear()
            if (iterations % 100 == 0) numbers = (0 until 4).map { (1..10).random() }
            val possibleOperators = charArrayOf('=', '+', '-', '*', '/', '%', '^')
            val operators = (0 until (3)).map { possibleOperators[(0 until 5).random()] }
            numbers.forEach {
                sb.append(it.toString())
            }
            operators.forEach {
                sb.append(it.toString())
            }
            puzzle = Puzzle(sb.toString())
            iterations++
            println(puzzle.operators)
        }
        println("found it")
    }
}