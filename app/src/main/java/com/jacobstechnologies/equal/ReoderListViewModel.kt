package com.jacobstechnologies.equal

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.burnoutcrew.reorderable.ItemPosition

class ReorderListViewModel(data : List<OperatorCardData>) : ViewModel() {

    var cardData by
        mutableStateOf(List(data.size) {
        OperatorCardData(value = data[it].value, key = "id$it")
    })

    fun move(to: ItemPosition, from: ItemPosition){
        cardData = cardData.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }
}