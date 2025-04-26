package com.alterpat.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date
import android.app.DatePickerDialog

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        labelInput.addTextChangedListener {
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            if(it!!.count() > 0)
                amountLayout.error = null
        }

        // Обработчик клика для выбора даты
        dateInputAdd.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInputAdd.setText(selectedDate)
            }, year, month, day).show()
        }

        // Установка текущей даты по умолчанию
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        dateInputAdd.setText(currentDate)

        // Обработчик клика для кнопки добавления транзакции
        addTransactionBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val date = dateInputAdd.text.toString() // Получаем дату из поля
            val article = articleSpinnerAdd.selectedItemPosition + 1 // Получаем выбранную статью

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                val transaction = Transaction( // Создание объекта Transaction
                    0,
                    label,
                    amount,
                    description,
                    parseDate(date), // Преобразуем дату в Long
                    article
                )
                insert(transaction) // сохранение в базу данных (но не в локальный список)
            }
        }
        // Обработчик клика для кнопки закрытия
        closeBtn.setOnClickListener {
            finish()
        }
    }

    // Метод для преобразования даты из строки в timestamp
    private fun parseDate(dateString: String): Long? {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            date?.time
        } catch (e: Exception) {
            null
        }
    }

    // функция сохранения созданного объекта новой странзакции в в базу данных SQLite (а не в локальный список).
    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transaction)
            finish()
        }
    }

}