package com.android.client.ninjacat.screens.admin.sale

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.models.Sale
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoField
import java.util.*

class SaleAdapter(
    val context: Context, var items: List<Sale>,
    val editCallback: (Sale) -> Unit,
    val deleteCallback: (Sale) -> Unit
) : RecyclerView.Adapter<SaleAdapter.SaleHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.sale_item,
            parent,
            false
        )
        return SaleHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SaleHolder, position: Int) {
        holder.bind(items[position], context)
    }

    fun setData(newData: List<Sale>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class SaleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val saleName = itemView.findViewById<TextView>(R.id.expenseName)
        private val saleQuantity = itemView.findViewById<TextView>(R.id.saleQuantity)
        private val saleAmount = itemView.findViewById<TextView>(R.id.saleAmount)
        private val saleDate = itemView.findViewById<TextView>(R.id.saleDate)
        private val editSaleBtn = itemView.findViewById<ImageView>(R.id.editSaleBtn)
        private val deleteSaleBtn = itemView.findViewById<ImageView>(R.id.deleteSaleBtn)

        fun bind(sale: Sale, context: Context) {
            saleName.text = sale.name
            saleQuantity.text = sale.quantity.toString()
            saleAmount.text = sale.amount.toString()
            val l = Locale("ru", "RU")
            if (sale.saleDate.isBefore(OffsetDateTime.now().with(ChronoField.HOUR_OF_DAY, 0))) {
                saleDate.text = sale.saleDate.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(l)
                )
            } else {
                saleDate.text = sale.saleDate.format(
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(l)
                )
            }
            editSaleBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) editCallback(items[adapterPosition])
            }
            deleteSaleBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) deleteCallback(items[adapterPosition])
            }
        }
    }
}