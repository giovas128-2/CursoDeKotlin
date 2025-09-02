package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades


sealed class TaskCategory(var isSelected: Boolean = false) {

    object Personal: TaskCategory()

    object Business : TaskCategory()

    object Other : TaskCategory()

    data class Custom(val name: String, val colorResId: Int) : TaskCategory()
}
