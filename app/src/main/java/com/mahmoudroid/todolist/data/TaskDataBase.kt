package com.mahmoudroid.todolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mahmoudroid.todolist.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
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
                dao.insert(Task("First1"))
                dao.insert(Task("First2"))
                dao.insert(Task("First3", completed = true))
                dao.insert(Task("First4"))
                dao.insert(Task("First5", completed = false))
                dao.insert(Task("First6"))
            }

        }
    }
}