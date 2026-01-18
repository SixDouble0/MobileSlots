package com.example.slotsgame.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.slotsgame.data.local.SpinHistory
import com.example.slotsgame.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items: List<SpinHistory> = emptyList()

    fun submitList(newItems: List<SpinHistory>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SpinHistory) {
            binding.tvWinAmount.text = if (item.winAmount > 0) "+ ${item.winAmount} $" else "0.0 $"
            binding.tvSymbols.text = "Układ: ${item.symbols.replace("|", " ")}"

            // Formatowanie daty
            val sdf = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(item.timestamp))
        }
    }
}