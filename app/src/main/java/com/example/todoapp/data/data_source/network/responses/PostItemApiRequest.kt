package com.example.todoapp.data.data_source.network.responses

import com.google.gson.annotations.SerializedName

data class PostItemApiRequest(
    @SerializedName("element")
    val item: TodoItemResponse,
)
