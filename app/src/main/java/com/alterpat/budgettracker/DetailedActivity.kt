package com.alterpat.budgettracker

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Spinner
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_add_transaction.amountInput
import kotlinx.android.synthetic.main.activity_add_transaction.amountLayout
import kotlinx.android.synthetic.main.activity_add_transaction.closeBtn
import kotlinx.android.synthetic.main.activity_add_transaction.descriptionInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelLayout
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date


// кнопка Update transaction появляется сразу, а раньше
// до того как я добавил обработчики изменений (addTextChangedListener) для к Spinner (категория) и dateInput (дата)
// она появлялась полько после изменеия полей Label, Amount, Description

// Причина:
//Кнопка "Обновить" становится видимой сразу после применения предложенных изменений,
// потому что обработчики событий для Spinner и dateInput активируются при инициализации элементов ,
// а не только при взаимодействии пользователя с ними. Это связано с тем,
// как работают обработчики событий в Android

// хотел решить через добавление флага, но все стало лагать, а кнопка так и стала появлятся сразу)))

// другими словами:
// Исправил баг, при котором при изменении в транзакции даты или категории не появлялась кнопка apdate,
// а при изменении других полей эта кнопка поялялась сразу.
// НО, теперь видимость у этой кнопки появляется сразу так как поля дата и категория
// инициализируясь - обновляются, это делает кнопку update transaction видимой сразу



class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction : Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        transaction = intent.getSerializableExtra("transaction") as Transaction

        // Инициализация полей
        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)


        // Инициализация даты
        transaction.date?.let {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateInput.setText(sdf.format(Date(it)))
        }


        // Настройка выбора даты
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateInput.setText(selectedDate)
                updateBtn.visibility = View.VISIBLE // Делаем кнопку видимой при изменении даты
            }, year, month, day).show()
        }

        // Если нужно отслеживать ручной ввод даты
        dateInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }

        // Настройка Spinner для article / Инициализация статьи
        val spinner = findViewById<Spinner>(R.id.articleSpinner)
        spinner.setSelection(transaction.article?.minus(1) ?: 0) // Установка выбранного значения

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateBtn.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ничего не делать
            }
        }


        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        // Для labelInput, amountInput и descriptionInput добавлены обработчики через метод addTextChangedListener. Этот метод вызывается каждый раз, когда текст в поле изменяется.
        labelInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                labelLayout.error = null
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if(it!!.count() > 0)
                amountLayout.error = null
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }





        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = descriptionInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val date = dateInput.text.toString() // Получаем дату
            val article = articleSpinner.selectedItemPosition + 1 // Получаем выбранную статью

            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if(amount == null)
                amountLayout.error = "Please enter a valid amount"
            else {
                val transaction = Transaction(
                    transaction.id,
                    label,
                    amount,
                    description,
                    parseDate(date), // Преобразуем дату в Long
                    article
                )
                update(transaction)
            }
        }

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



    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()
        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
        }
    }

}
