package com.shujiu.zaky3d.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    /**
     * 聊天列表显示
     */
    val chatListToggle = MutableLiveData(true)

    /**
     * 软键盘展示
     */
    val inputToggle = MutableLiveData(false)

    fun chatListToggle() {
        chatListToggle.value = chatListToggle.value?.not()
    }

    fun inputToggle(show:Boolean) {
        inputToggle.value = show
    }
}