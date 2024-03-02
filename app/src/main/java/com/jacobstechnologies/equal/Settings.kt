package com.jacobstechnologies.equal

import android.content.Context

class Settings(context: Context){
    private val sharedPreferencesString = "shared_preferences"

    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesString, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    fun loadInt(sharedPreferencesSettings : SharedPreferencesSettings, default: Int) : Int{
        return sharedPreferences.getInt(sharedPreferencesSettings.name, default)
    }

    fun saveInt(sharedPreferencesSettings : SharedPreferencesSettings, newValue: Int) {
        editor.putInt(sharedPreferencesSettings.name, newValue).apply()
    }

    fun loadString(sharedPreferencesSettings : SharedPreferencesSettings, default: String): String{
        return sharedPreferences.getString(sharedPreferencesSettings.name, default)!!
    }

    fun saveString(sharedPreferencesSettings : SharedPreferencesSettings, newString: String){
        editor.putString(sharedPreferencesSettings.name, newString).apply()
    }

    fun loadBool(sharedPreferencesSettings : SharedPreferencesSettings, default: Boolean): Boolean{
        return sharedPreferences.getBoolean(sharedPreferencesSettings.name, default)
    }

    fun saveBool(sharedPreferencesSettings : SharedPreferencesSettings, newValue:Boolean){
        editor.putBoolean(sharedPreferencesSettings.name, newValue).apply()
    }

    fun savePuzzleSettingsString(sharedPreferencesSettings : SharedPreferencesSettings, difficultyInt: Int, newValue: String){
        editor.putString(sharedPreferencesSettings.name + difficultyInt, newValue).apply()
    }

    fun loadPuzzleSettingsString(sharedPreferencesSettings : SharedPreferencesSettings, difficultyInt: Int, default: String) : String{
        return sharedPreferences.getString(sharedPreferencesSettings.name + difficultyInt, default)!!
    }

    enum class SharedPreferencesSettings(key: String){
        DIFFICULTY("difficulty_key"), VIBRATION("vibration_key"), SAVED_PUZZLES("saved_puzzles")
    }

}