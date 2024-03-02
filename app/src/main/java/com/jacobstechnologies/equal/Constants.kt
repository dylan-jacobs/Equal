package com.jacobstechnologies.equal

const val moveVibrationLength = 50L
const val winVibrationLength = 1000L
const val switchVibrationLength = 50L

val easyPuzzleSettings: PuzzleSettings = PuzzleSettings(title = "Easy", numberCount = 3, maxDigit = 5, possibleOperators = charArrayOf('=', '+', '-', '*', '/'))
val mediumPuzzleSettings: PuzzleSettings = PuzzleSettings(title = "Medium", numberCount = 4, maxDigit = 9, possibleOperators = charArrayOf('=', '+', '-', '*', '/'))
val difficultPuzzleSettings: PuzzleSettings = PuzzleSettings(title = "Difficult", numberCount = 5, maxDigit = 9, possibleOperators = charArrayOf('=', '+', '-', '*', '/', '^'))
val extremePuzzleSettings: PuzzleSettings = PuzzleSettings(title = "Extreme", numberCount = 6, maxDigit = 9, possibleOperators = charArrayOf('=', '+', '-', '*', '/', '^'))

val puzzleSettingsPresets = arrayOf(easyPuzzleSettings, mediumPuzzleSettings, difficultPuzzleSettings, extremePuzzleSettings)