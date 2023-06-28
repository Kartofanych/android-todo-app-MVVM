package com.example.todoapp.repository

import android.util.Log
import com.example.todoapp.network.Common
import com.example.todoapp.network.NetworkAccess
import com.example.todoapp.network.responses.PatchListApiRequest
import com.example.todoapp.network.responses.PostItemApiRequest
import com.example.todoapp.network.responses.PostItemApiResponse
import com.example.todoapp.network.responses.TodoItemResponse
import com.example.todoapp.room.ToDoItemEntity
import com.example.todoapp.room.TodoItem
import com.example.todoapp.room.TodoListDatabase
import com.example.todoapp.shared_preferences.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class ItemsRepository(
    db: TodoListDatabase,
    private val sharedPreferencesHelper: SharedPreferencesHelper
) {

    private val dao = db.listDao

    fun getAllData(): Flow<List<TodoItem>> =
        dao.getAllFlow().map { list -> list.map { it.toItem() } }

    fun getItem(itemId: String): TodoItem = dao.getItem(itemId).toItem()

    suspend fun addItem(todoItem: TodoItem) {
        val toDoItemEntity = ToDoItemEntity.fromItem(todoItem)
        return dao.add(toDoItemEntity)
    }

    suspend fun deleteItem(todoItem: TodoItem) {
        val toDoItemEntity = ToDoItemEntity.fromItem(todoItem)
        return dao.delete(toDoItemEntity)
    }

    suspend fun changeItem(todoItem: TodoItem) {
        val toDoItemEntity = ToDoItemEntity.fromItem(todoItem)
        return dao.updateItem(toDoItemEntity)
    }

    suspend fun changeDone(id: String, done: Boolean) {
        return dao.updateDone(id, done, System.currentTimeMillis())
    }


    private val service = Common.retrofitService

    suspend fun getNetworkData() {
        val networkListResponse = service.getList()


        if (networkListResponse.isSuccessful) {
            val body = networkListResponse.body()
            if (body != null) {
                val networkList = body.list
                val currentList = dao.getAll().map { TodoItemResponse.fromItem(it.toItem()) }
                val mergedList = HashMap<String, TodoItemResponse>()

                for(item in networkList){
                    mergedList[item.id] = item
                    Log.d("1", "${item.id} ${item.dateChanged}")
                }
                for (item in currentList) {
                    if (mergedList.containsKey(item.id)) {
                        val item1 = mergedList[item.id]
                        if (item.dateChanged > item1!!.dateChanged) {
                            mergedList[item.id] = item
                        }else{
                            mergedList[item.id] = item1
                        }
                    }else{
                        mergedList[item.id] = item
                    }
                }

                updateNetworkList(mergedList.values.toList())
            }
        }
    }

    private suspend fun updateNetworkList(mergedList: List<TodoItemResponse>) {

        val updateResponse = service.updateList(
            sharedPreferencesHelper.getLastRevision(),
            PatchListApiRequest(mergedList)
        )


        if (updateResponse.isSuccessful) {
            val responseBody = updateResponse.body()
            if (responseBody != null) {
                sharedPreferencesHelper.putRevision(responseBody.revision)
                updateRoom(responseBody.list)
            }
        }
    }

    private suspend fun updateRoom(response: List<TodoItemResponse>) {
        val list = response.map { it.toItem() }
        dao.addList(list.map { ToDoItemEntity.fromItem(it) })
    }

    suspend fun postNetworkItem(
        lastRevision: Int,
        newItem: TodoItem
    ): NetworkAccess<PostItemApiResponse> {
        val postResponse = service.postElement(
            lastRevision,
            PostItemApiRequest(TodoItemResponse.fromItem(newItem))
        )

        if (postResponse.isSuccessful) {
            val responseBody = postResponse.body()
            if (responseBody != null) {
                return NetworkAccess.Success(responseBody)
            }
        }
        return NetworkAccess.Error(postResponse)
    }

    suspend fun deleteNetworkItem(
        lastRevision: Int,
        id: String
    ): NetworkAccess<PostItemApiResponse> {
        val postResponse = service.deleteElement(id, lastRevision)

        if (postResponse.isSuccessful) {
            val responseBody = postResponse.body()
            if (responseBody != null) {
                return NetworkAccess.Success(responseBody)
            }
        }
        return NetworkAccess.Error(postResponse)
    }

    suspend fun updateNetworkItem(
        lastRevision: Int,
        item: TodoItem
    ) = withContext(Dispatchers.IO) {

        val updateItemResponse = service.updateElement(
            item.id, lastRevision, PostItemApiRequest(
                TodoItemResponse.fromItem(item)
            )
        )
        if (updateItemResponse.isSuccessful) {
            val body = updateItemResponse.body()
            if (body != null) {
                sharedPreferencesHelper.putRevision(body.revision)
            }
        }
    }


}