package com.android.client.ninjacat.screens.admin.expense

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.models.Expense

class ExpenseAdapter(
    val context: Context,
    var items: List<Expense>,
    val editCallback: (Expense) -> Unit,
    val deleteCallback: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.expense_item,
            parent,
            false
        )
        return ExpenseHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        holder.bind(items[position], context)
    }

    fun setData(newData: List<Expense>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class ExpenseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val expenseName = itemView.findViewById<TextView>(R.id.expenseName)
        private val deleteExpenseBtn = itemView.findViewById<ImageView>(R.id.deleteExpenseItemBtn)

        fun bind(expense: Expense, context: Context) {
            expenseName.text = expense.name
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) editCallback(items[adapterPosition])
            }
            deleteExpenseBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) deleteCallback(items[adapterPosition])
            }

        }
    }
}