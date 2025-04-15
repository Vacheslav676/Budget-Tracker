package com.alterpat.budgettracker

//androidx.room.*: Используется для работы с Room, библиотекой для работы с базами данных SQLite.
//@Entity: Аннотация, которая указывает, что этот класс представляет таблицу в базе данных.
//@PrimaryKey: Аннотация, которая помечает поле как первичный ключ.
//@ColumnInfo: Аннотация, которая связывает поле класса с колонкой в таблице.
//@Ignore: Аннотация, которая указывает Room игнорировать поле или метод при работе с базой данных.
//java.io.Serializable: Интерфейс, который позволяет объектам этого класса быть сериализуемыми (например, для передачи через Intent).

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable


//ласс помечен аннотацией @Entity, что делает его сущностью для Room.
// Параметр tableName = "transactions" указывает имя таблицы в базе данных.
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int,       // @PrimaryKey : Указывает, что это поле является первичным ключом таблицы. autoGenerate = true  Означает, что значения для этого поля будут автоматически генерироваться базой данных (обычно это автоинкремент).
    val label: String,
    val amount: Double,
    val description: String): Serializable {
}


