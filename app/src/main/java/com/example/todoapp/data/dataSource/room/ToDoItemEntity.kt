package com.example.todoapp.data.dataSource.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.domain.model.Importance
import com.example.todoapp.domain.model.TodoItem
import java.sql.Date

@Entity(tableName = "todoList")
data class ToDoItemEntity(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "importance") var importance: Importance,
    @ColumnInfo(name = "deadline") var deadline: Long?,
    @ColumnInfo(name = "done") var done: Boolean,
    @ColumnInfo(name = "createdAt") val createdAt: Long,
    @ColumnInfo(name = "changedAt") var changedAt: Long?
) {
    fun toItem(): TodoItem = TodoItem(
        id,
        description,
        importance,
        deadline?.let { Date(it) },
        done,
        Date(createdAt),
        changedAt?.let { Date(it) }
    )

    companion object {
        fun fromItem(toDoItem: TodoItem): ToDoItemEntity {
            return ToDoItemEntity(
                id = toDoItem.id,
                description = toDoItem.text,
                importance = toDoItem.importance,
                deadline = toDoItem.deadline?.time,
                done = toDoItem.done,
                createdAt = toDoItem.dateCreation.time,
                changedAt = toDoItem.dateChanged?.time
            )
        }
    }
}
