package com.example.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.ActivityMainBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.tasks.observe(this, Observer { tasks ->
            taskAdapter = TaskAdapter(
                tasks,
                { task -> editTask(task) },
                { task -> viewModel.updateTask(task) },
                { task -> viewModel.deleteTask(task) }
            )
            binding.recyclerView.adapter = taskAdapter
        })

        binding.buttonAdd.setOnClickListener {
            showAddEditTaskDialog(null)
        }
    }

    private fun showAddEditTaskDialog(task: Task?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val editTextTask = dialogView.findViewById<EditText>(R.id.editTextTask)
        if (task != null) {
            editTextTask.setText(task.title)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (task == null) "Add Task" else "Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val taskTitle = editTextTask.text.toString()
                if (taskTitle.isNotEmpty()) {
                    if (task == null) {
                        val newTask = Task(id = System.currentTimeMillis(), title = taskTitle)
                        viewModel.addTask(newTask)
                    } else {
                        task.title = taskTitle
                        viewModel.updateTask(task)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun editTask(task: Task) {
        showAddEditTaskDialog(task)
    }
}