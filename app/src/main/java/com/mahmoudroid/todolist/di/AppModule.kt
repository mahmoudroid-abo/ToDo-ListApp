package com.mahmoudroid.todolist.di

import android.app.Application
import androidx.room.Room
import com.mahmoudroid.todolist.data.TaskDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application, callback: TaskDataBase.Callback) =
        Room.databaseBuilder(app, TaskDataBase::class.java, "task_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()


    @Provides
    fun provideTaskDao(db: TaskDataBase) = db.taskDao()
}