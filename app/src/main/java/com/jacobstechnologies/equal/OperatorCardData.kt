package com.jacobstechnologies.equal

data class OperatorCardData(val value: Char, val key: String){
    var isEmpty: Boolean = value == ' '
}
