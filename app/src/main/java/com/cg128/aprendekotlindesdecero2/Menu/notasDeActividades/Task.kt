package com.cg128.aprendekotlindesdecero2.Menu.notasDeActividades

data class Task (val name:String,
                 val category: TaskCategory,
                 var isSelected: Boolean= false)