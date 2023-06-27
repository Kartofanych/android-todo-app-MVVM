package com.example.todoapp.network.responses

import com.google.gson.annotations.SerializedName

data class PatchListApiRequest(
    @SerializedName("list")
    val list: List<TodoItemResponse>
)