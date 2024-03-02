package com.jacobstechnologies.equal

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.reflect.Type

fun checkSolved(operators: List<OperatorCardData>) : Boolean {
    var dataString = ""
    operators.forEach{item ->
        dataString = dataString.plus(item.value)
    }
    val expressions = dataString.split("=") // split into two expressions to evaluate their equality

    if (expressions.size != 2){
        return false
    }

    if (expressions[0] == ""){
        return false
    }

    if (expressions[1] == ""){
        return false
    }

    if (expressions[0].startsWith("+") || expressions[0].startsWith("%") || expressions[0].startsWith("/") || expressions[0].startsWith("*")){
        return false
    }

    if (expressions[1].startsWith("+") || expressions[1].startsWith("%") || expressions[1].startsWith("/") || expressions[1].startsWith("*")) {
        return false
    }

    if (expressions[0].endsWith("+") || expressions[0].endsWith("%") || expressions[0].endsWith("/") || expressions[0].endsWith("*")){
        return false
    }

    if (expressions[1].endsWith("+") || expressions[1].endsWith("%") || expressions[1].endsWith("/") || expressions[1].endsWith("*")) {
        return false
    }

    return try {
        val e1 : Double = ExpressionBuilder(expressions[0])
            .build()
            .evaluate()
        val e2 : Double = ExpressionBuilder(expressions[1])
            .build()
            .evaluate()
        e1 == e2
    } catch (e: Exception){
        false
    }
}

fun <T> permutations(list: List<T>): Set<List<T>> {
    if (list.isEmpty()) return setOf(emptyList())

    val result: MutableSet<List<T>> = mutableSetOf()
    for (i in list.indices) {
        permutations(list - list[i]).forEach {
                item -> result.add(item + list[i])
        }
    }
    return result
}

fun checkIfValidPuzzle(puzzle : Puzzle) : Boolean{
    // rearrange numbers until a valid equation is reached
    val data = permutations(puzzle.operators)
    data.forEach{list ->
        if (checkSolved(list)){
            var dataString = ""
            list.forEach{item ->
                dataString = dataString.plus(item.value)
            }
            puzzle.solution = dataString
            return true
        }
    }
    return false
}

fun generatePuzzle(puzzleSettings : PuzzleSettings): Puzzle{ // numberCount is the number of numbers present in the equation (think of it as measuring difficulty)
    var puzzle = Puzzle("")
    val sb = StringBuilder()
    var numbers = (0 until puzzleSettings.numberCount).map { (0..puzzleSettings.maxDigit).random() }
    var iterations = 0
    while (!checkIfValidPuzzle(puzzle)) {
        sb.clear()
        if (iterations % 100 == 0) numbers = (0 until puzzleSettings.numberCount).map { (1 .. puzzleSettings.maxDigit).random() }
        val operators = (0 until puzzleSettings.numberCount - 1).map { puzzleSettings.possibleOperators.random() }
        numbers.forEach {
            sb.append(it.toString())
        }
        operators.forEach {
            sb.append(it.toString())
        }
        puzzle = Puzzle(sb.toString())
        iterations++
    }

    while (checkSolved(puzzle.operators)){
        puzzle.shuffle()
    }

    return puzzle
}

fun generateSmarterPuzzle(puzzleSettings : PuzzleSettings): Puzzle{
    var puzzle = Puzzle("")
    val sb = StringBuilder()
    var iterations = 0
    var operators = (0 until puzzleSettings.numberCount - 1).map { puzzleSettings.possibleOperators[(0 until puzzleSettings.possibleOperators.size).random()] }
    while (!checkIfValidPuzzle(puzzle)) {
        sb.clear()
        if (iterations % 100 == 0) operators = (0 until puzzleSettings.numberCount - 1).map { puzzleSettings.possibleOperators[(0 until puzzleSettings.possibleOperators.size).random()] }
        val numbers = (0 until puzzleSettings.numberCount).map { (0..puzzleSettings.maxDigit).random() }
        numbers.forEach {
            sb.append(it.toString())
        }
        operators.forEach {
            sb.append(it.toString())
        }
        puzzle = Puzzle(sb.toString())
        iterations++
    }

    while (checkSolved(puzzle.operators)){
        puzzle.shuffle()
    }

    return puzzle
}

fun loadMorePuzzles(context: Context) : Puzzle{
    val settings = Settings(context = context)
    val difficultyInt = settings.loadInt(Settings.SharedPreferencesSettings.DIFFICULTY, 0)
    val difficulty = puzzleSettingsPresets[difficultyInt]
    val gson = Gson()
    val json = settings.loadPuzzleSettingsString(Settings.SharedPreferencesSettings.SAVED_PUZZLES, difficultyInt, "")
    val type: Type = object : TypeToken<ArrayList<Puzzle?>?>() {}.type
    val list = try {
        gson.fromJson<Any>(json, type) as ArrayList<Puzzle>
    } catch(e: java.lang.Exception){
        Log.v("JSON LOAD ERROR", e.toString())
        ArrayList()
    }

    val puzzle =
        if (list.isEmpty()){
            generateSmarterPuzzle(difficulty)
        } else{
            list.removeAt(0)
        }

    val size = 10
    var index = list.size

    while (index < size) {
        CoroutineScope(Dispatchers.Default).launch {
            val newPuzzle = generateSmarterPuzzle(difficulty)
            list.add(newPuzzle)
            val newJson = gson.toJson(list)
            settings.savePuzzleSettingsString(Settings.SharedPreferencesSettings.SAVED_PUZZLES, difficultyInt, newJson)
            Log.v("jjj", "new")
        }
        index++
    }

    return puzzle
}


