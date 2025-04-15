package com.alterpat.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


// Класс TransactionAdapter наследуется от RecyclerView.Adapter и предназначен для управления данными, которые будут отображаться в RecyclerView
// Параметр transactions — это список объектов типа Transaction, который будет отображаться в списке.
//TransactionHolder — это внутренний класс, представляющий ViewHolder для элементов списка.
class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    //Класс TransactionHolder наследуется от RecyclerView.ViewHolder и используется для хранения ссылок на виджеты (например, TextView) внутри одного элемента списка.
    //label и amount — это ссылки на текстовые поля (TextView), которые будут обновляться для каждого элемента списка.
    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view) {
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
    }

    // Этот метод вызывается для создания нового ViewHolder, когда требуется новый элемент списка.
    //LayoutInflater используется для "надувания" макета элемента списка (R.layout.transaction_layout).
    //Созданный View передается в конструктор TransactionHolder.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        return TransactionHolder(view)
    }

    // Этот метод связывает данные с конкретным элементом списка.
    //position указывает на позицию текущего элемента в списке.
    //transaction — это объект Transaction, соответствующий текущей позиции.
    //Для положительных сумм (amount >= 0) текст форматируется как + $X.XX и окрашивается в зеленый цвет. Для отрицательных сумм текст форматируется как - $X.XX и окрашивается в красный цвет.
    //Поле label обновляется значением из объекта Transaction.
    //При нажатии на элемент списка создается Intent для перехода в активность DetailedActivity. В Intent передается объект transaction для отображения деталей.
    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if(transaction.amount >= 0){
            holder.amount.text = "+ %.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
        }else {
            holder.amount.text = "- %.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        }

        holder.label.text = transaction.label

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
            context.startActivity(intent)
        }
    }

    // Этот метод возвращает количество элементов в списке.
    //Размер списка определяется через transactions.size.
    override fun getItemCount(): Int {
        return transactions.size
    }

    // Этот метод используется для обновления данных в адаптере.
    //Новый список transactions заменяет старый, и вызывается notifyDataSetChanged(), чтобы уведомить RecyclerView о необходимости перерисовать список.
    fun setData(transactions: List<Transaction>){
        this.transactions = transactions
        notifyDataSetChanged()
    }
}