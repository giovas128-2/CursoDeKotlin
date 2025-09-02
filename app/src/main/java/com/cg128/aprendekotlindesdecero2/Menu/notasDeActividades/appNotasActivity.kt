package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades.TaskCategory.*
import com.cg128.aprendekotlindesdecero2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class appNotasActivity : AppCompatActivity() {

    private val categories = mutableListOf<TaskCategory>(
        TaskCategory.Business,
        TaskCategory.Personal,
        TaskCategory.Other // siempre al final
    )

    private val tasks = mutableListOf(
        Task("Negocios", Business) ,
        Task("Personal", Personal) ,
        Task("Otro", Other)

    )
    private var lastSelectedCategoryIndex: Int? = null
    private lateinit var rvCategory: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var rvTasks : RecyclerView
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var fadAddTask: FloatingActionButton

    private lateinit var fadDeleteTask: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_app_notas)
        initComponents()
        initIU()
        initListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initListeners() {
        fadAddTask.setOnClickListener { showDialog() }
        fadDeleteTask.setOnClickListener { showDeleteDialog() }

    }
    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_fask)

        val btnAddTask: Button = dialog.findViewById(R.id.btnAddTask)
        val etTask: EditText = dialog.findViewById(R.id.etTask)
        val rgCategories: RadioGroup = dialog.findViewById(R.id.rgCategories)

        // Limpiar radio group por si se reutiliza
        rgCategories.removeAllViews()

        // Generar radio buttons dinámicamente
        categories.forEachIndexed { index, category ->
            val radioButton = RadioButton(this)
            radioButton.id = View.generateViewId()

            radioButton.text = when (category) {
                TaskCategory.Business -> getString(R.string.todo_dialog_category_business)
                TaskCategory.Personal -> getString(R.string.todo_dialog_category_personal)
                TaskCategory.Other -> getString(R.string.todo_dialog_category_other)
                is TaskCategory.Custom -> category.name
            }

            val colorResId = when (category) {
                TaskCategory.Business -> R.color.todo_business_category
                TaskCategory.Personal -> R.color.todo_personal_category
                TaskCategory.Other -> R.color.todo_other_category
                is TaskCategory.Custom -> category.colorResId
            }

            radioButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, colorResId))
            radioButton.tag = category // Guardamos el objeto para usarlo después

            rgCategories.addView(radioButton)

            // Seleccionar por defecto el primero
            if (index == 0) radioButton.isChecked = true
        }

        // Botón para crear tarea
        btnAddTask.setOnClickListener {
            val taskText = etTask.text.toString().trim()
            if (taskText.isNotEmpty()) {
                val selectedId = rgCategories.checkedRadioButtonId
                val selectedRadioButton = rgCategories.findViewById<RadioButton>(selectedId)
                val selectedCategory = selectedRadioButton.tag as TaskCategory

                tasks.add(Task(taskText, selectedCategory))
                updateTasks()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_delete_fask)

        val etCategoryName = dialog.findViewById<EditText>(R.id.etCategoryName)
        val layoutTasks = dialog.findViewById<LinearLayout>(R.id.layoutTasks)
        val btnDelete = dialog.findViewById<Button>(R.id.btnAddTask)

        val checkBoxes = mutableListOf<CheckBox>() // guardamos los checkbox creados
        var filteredTasks = listOf<Task>() // para saber qué tareas están visibles

        fun updateTaskListForCategory(categoryName: String) {
            layoutTasks.removeAllViews()
            checkBoxes.clear()

            val category = categories.find {
                (it is TaskCategory.Custom && it.name.equals(categoryName, ignoreCase = true)) ||
                        (it == TaskCategory.Business && categoryName.equals(getString(R.string.todo_dialog_category_business), ignoreCase = true)) ||
                        (it == TaskCategory.Personal && categoryName.equals(getString(R.string.todo_dialog_category_personal), ignoreCase = true)) ||
                        (it == TaskCategory.Other && categoryName.equals(getString(R.string.todo_dialog_category_other), ignoreCase = true))
            }

            if (category != null) {
                filteredTasks = tasks.filter { it.category == category }

                filteredTasks.forEach { task ->
                    val checkBox = CheckBox(this)
                    checkBox.text = task.name
                    checkBox.setTextColor(ContextCompat.getColor(this, R.color.white))
                    layoutTasks.addView(checkBox)
                    checkBoxes.add(checkBox)
                }
            } else {
                filteredTasks = emptyList()
            }
        }

        // Este TextWatcher hace que se actualice la lista al escribir
        etCategoryName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateTaskListForCategory(s.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Este botón elimina las tareas seleccionadas
        btnDelete.setOnClickListener {
            val tasksToRemove = filteredTasks.filterIndexed { index, _ -> checkBoxes.getOrNull(index)?.isChecked == true }
            tasks.removeAll(tasksToRemove)
            updateTasks()
            dialog.dismiss()
        }

        dialog.show()
    }





    private fun initComponents() {
        rvCategory = findViewById(R.id.rvCategories)
        rvTasks = findViewById(R.id.rvTasks)
        fadAddTask = findViewById(R.id.fabAddTask)
        fadDeleteTask = findViewById(R.id.fabDeleteTask)
    }
    private fun initIU() {
        categoriesAdapter = CategoriesAdapter(
            this,
            categories,
            { position -> updateCategories(position) },
            { position -> deleteCategory(position) }
        )
        rvCategory.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false)
        rvCategory.adapter = categoriesAdapter

        tasksAdapter = TasksAdapter(tasks) {task -> onItemSelected(task)}
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = tasksAdapter
    }

    private fun onItemSelected(task: Task){
        task.isSelected = !task.isSelected
        updateTasks()
    }

    private fun updateCategories(position: Int){
        val currentCategory = categories[position]

        // Si seleccionó "Otro", mostrar diálogo
        if (currentCategory is TaskCategory.Other) {
            showAddCustomCategoryDialog()
            return
        }

        // Desactiva la categoría anterior
        lastSelectedCategoryIndex?.let { previousIndex ->
            if (previousIndex != position) {
                categories[previousIndex].isSelected = false
                categoriesAdapter.notifyItemChanged(previousIndex)
            }
        }

        // Temporalmente ponerla como desactivada
        currentCategory.isSelected = false
        categoriesAdapter.notifyItemChanged(position)

        // Espera 500ms y luego cambia a seleccionada
        rvCategory.postDelayed({
            currentCategory.isSelected = true
            categoriesAdapter.notifyItemChanged(position)
            updateTasks()
        }, 200)

        lastSelectedCategoryIndex = position
    }

    private fun updateTasks() {

        rvTasks.post {
            val selectedCategories:List<TaskCategory> = categories.filter { it.isSelected }
            val newTasks = tasks.filter { selectedCategories.contains(it.category) }
            tasksAdapter.tasks = newTasks
            tasksAdapter.notifyDataSetChanged()
        }
    }

    private fun showAddCustomCategoryDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_category)

        val etTask = dialog.findViewById<EditText>(R.id.etTask)
        val rgColors = dialog.findViewById<RadioGroup>(R.id.rgCategoriesColors)
        val btnAddTask = dialog.findViewById<Button>(R.id.btnAddTask)

        btnAddTask.setOnClickListener {
            val name = etTask.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener

            val selectedColorId = when (rgColors.checkedRadioButtonId) {
                R.id.rbWhite -> R.color.todo_white_category
                R.id.rbBlack -> R.color.todo_black_category
                R.id.rbPurple -> R.color.todo_purple_category
                R.id.rbPink -> R.color.todo_pink_category
                R.id.rbBlue -> R.color.todo_blue_category
                R.id.rbGreen -> R.color.todo_green_category
                R.id.rbYellow -> R.color.todo_yellow_category
                R.id.rbOrange -> R.color.todo_orange_category
                R.id.rbRed -> R.color.todo_red_category
                else -> R.color.todo_white_category
            }

            val newCategory = TaskCategory.Custom(name, selectedColorId)

            // Insertar antes de la categoría "Otro"
            val otherIndex = categories.indexOfFirst { it is TaskCategory.Other }
            if (otherIndex != -1) {
                (categories as MutableList).add(otherIndex, newCategory)
                categoriesAdapter.notifyItemInserted(otherIndex)
            }

            dialog.dismiss()
        }

        dialog.show()
    }
    private fun deleteCategory(position: Int) {
        val categoryToDelete = categories[position]

        if (categoryToDelete is TaskCategory.Other) {
            Toast.makeText(this, "No se puede eliminar la categoría 'Otro'", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Eliminar categoría")
            .setMessage("¿Estás seguro de que deseas eliminar esta categoría y todas sus tareas?")
            .setPositiveButton("Eliminar") { _, _ ->
                categories.removeAt(position)

                tasks.removeAll { task ->
                    when {
                        categoryToDelete is TaskCategory.Custom && task.category is TaskCategory.Custom ->
                            task.category.name == categoryToDelete.name
                        categoryToDelete === TaskCategory.Personal -> task.category === TaskCategory.Personal
                        categoryToDelete === TaskCategory.Business -> task.category === TaskCategory.Business
                        else -> false
                    }
                }

                categoriesAdapter.notifyItemRemoved(position)
                updateTasks()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }




}
