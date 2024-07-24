package com.example.todoapp

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView

data class Task(
    var id: Long = 0,
    var title: String,
    var isCompleted: Boolean = false
)


class TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val tasksLiveData = MutableLiveData<List<Task>>()

    init {
        tasksLiveData.value = tasks
    }

    fun getTasks(): LiveData<List<Task>> = tasksLiveData

    fun addTask(task: Task) {
        tasks.add(task)
        tasksLiveData.value = tasks
    }

    fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            tasksLiveData.value = tasks
        }
    }

    fun deleteTask(task: Task) {
        tasks.remove(task)
        tasksLiveData.value = tasks
    }
}


class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository()
    val tasks: LiveData<List<Task>> = repository.getTasks()

    fun addTask(task: Task) {
        repository.addTask(task)
    }

    fun updateTask(task: Task) {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) {
        repository.deleteTask(task)
    }
}


class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskClicked: (Task) -> Unit,
    private val onCheckBoxClicked: (Task) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(task: Task) {
            taskTitle.text = task.title
            taskCheckBox.isChecked = task.isCompleted
            itemView.setOnClickListener { onTaskClicked(task) }
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onCheckBoxClicked(task)
            }
            buttonDelete.setOnClickListener { onDeleteClicked(task) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}

