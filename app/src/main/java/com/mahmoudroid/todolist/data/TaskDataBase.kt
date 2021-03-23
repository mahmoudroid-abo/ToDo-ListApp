package com.mahmoudroid.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mahmoudroid.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Task::class], version = 1)
abstract class TaskDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val dataBase: Provider<TaskDataBase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //database operations
            val dao = dataBase.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("WorkOut", important = true))
                dao.insert(Task("Buy Grocery"))
                dao.insert(Task("Study Grammar"))
                dao.insert(Task("Learn New Deutsch Words", important = true))
                dao.insert(Task("Watch Movie"))
                dao.insert(Task("Teeth brushing", important = true))
                dao.insert(Task("Study Hilt,", important = true, completed = true))
            }
        }
    }
}