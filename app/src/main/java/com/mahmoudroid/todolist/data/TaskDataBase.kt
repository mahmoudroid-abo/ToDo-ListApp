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
                dao.insert(Task("First", important = true))
                dao.insert(Task("Second"))
                dao.insert(Task("Grammar"))
                dao.insert(Task("Buy"))
                dao.insert(Task("Deliver"))
                dao.insert(Task("A"))
                dao.insert(Task("English"))
                dao.insert(Task("Mahmoud,", important = true, completed = true))
                dao.insert(Task("Study", completed = true))
                dao.insert(Task("Deutsch", important = true))
                dao.insert(Task("Ali"))
                dao.insert(Task("First", completed = true))
            }

        }
    }
}