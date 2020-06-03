package com.android.client.ninjacat.screens.admin.charge

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.models.Charge
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoField
import java.util.*

class ChargeAdapter(
    val context: Context,
    var items: List<Charge>,
    val editCallback: (Charge) -> Unit,
    val deleteCallback: (Charge) -> Unit
) : RecyclerView.Adapter<ChargeAdapter.ChargeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.charge_item,
            parent,
            false
        )
        return ChargeHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ChargeHolder, position: Int) {
        holder.bind(items[position], context)
    }

    fun setData(newData: List<Charge>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class ChargeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chargeAmount = itemView.findViewById<TextView>(R.id.chargeAmount)
        private val chargeDate = itemView.findViewById<TextView>(R.id.chargeDate)
        private val editChargeBtn = itemView.findViewById<ImageView>(R.id.editChargeBtn)
        private val deleteChargeBtn = itemView.findViewById<ImageView>(R.id.deleteChargeBtn)

        fun bind(charge: Charge, context: Context) {
            chargeAmount.text = charge.amount.toString()
            val l = Locale("ru", "RU")
            if (charge.chargeDate.isBefore(OffsetDateTime.now().with(ChronoField.HOUR_OF_DAY, 0))) {
                chargeDate.text = charge.chargeDate.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(l)
                )
            } else {
                chargeDate.text = charge.chargeDate.format(
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(l)
                )
            }
            editChargeBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) editCallback(items[adapterPosition])
            }
            deleteChargeBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) deleteCallback(items[adapterPosition])
            }
        }
    }
}