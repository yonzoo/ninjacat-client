package com.android.client.ninjacat.screens.client.product

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.client.ninjacat.R
import com.android.client.ninjacat.core.room.models.Product

class ProductAdapter(val context: Context, var items: List<Product>, val callback: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.product_item,
            parent,
            false
        )
        return ProductHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(items[position], context)
    }

    fun setData(newData: List<Product>) {
        items = newData
        notifyDataSetChanged()
    }

    inner class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName = itemView.findViewById<TextView>(R.id.productName)
        private val productQuantity = itemView.findViewById<TextView>(R.id.productQuantity)
        private val productAmount = itemView.findViewById<TextView>(R.id.productAmount)
        private val editProductBtn = itemView.findViewById<ImageView>(R.id.editProductBtn)
        private val deleteProductBtn = itemView.findViewById<ImageView>(R.id.deleteProductBtn)

        fun bind(product: Product, context: Context) {
            productName.text = product.name
            productQuantity.text = product.quantity.toString()
            productAmount.text = product.amount.toString()
            editProductBtn.visibility = View.GONE
            deleteProductBtn.visibility = View.GONE
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) callback(items[adapterPosition])
            }
        }
    }
}