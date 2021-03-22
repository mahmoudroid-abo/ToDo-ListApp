package com.mahmoudroid.todolist.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mahmoudroid.todolist.data.PreferencesManager
import com.mahmoudroid.todolist.data.SortOrder
import com.mahmoudroid.todolist.data.Task
import com.mahmoudroid.todolist.data.TaskDao
import com.mahmoudroid.todolist.ui.ADD_TASK_RESULT_OK
import com.mahmoudroid.todolist.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {


    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskEventChannel = Channel<TasksEvent>()

    val taskEvent = taskEventChannel.receiveAsFlow()

    private val taskFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToEditTasksScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))

    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTasksMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToAddTasksScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.ShowTasksSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }


    sealed class TasksEvent {
        object NavigateToAddTasksScreen : TasksEvent()
        data class NavigateToEditTasksScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTasksMessage(val task: Task) : TasksEvent()
        data class ShowTasksSavedConfirmationMessage(val msg: String) : TasksEvent()

        object NavigateToDeleteAllCompletedScreen : TasksEvent()
    }
}

