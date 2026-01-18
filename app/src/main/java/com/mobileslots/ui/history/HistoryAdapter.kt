package com.mobileslots.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mobileslots.R
import com.mobileslots.domain.model.GameHistory
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<GameHistory, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val gameTypeText: TextView = itemView.findViewById(R.id.game_type_text)
        private val betAmountText: TextView = itemView.findViewById(R.id.bet_amount_text)
        private val winAmountText: TextView = itemView.findViewById(R.id.win_amount_text)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)
        private val card: MaterialCardView = itemView.findViewById(R.id.history_card)

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        fun bind(history: GameHistory) {
            gameTypeText.text = history.gameType.name
            betAmountText.text = itemView.context.getString(R.string.history_bet_amount, history.betAmount)
            winAmountText.text = itemView.context.getString(R.string.history_win_amount, history.winAmount)
            dateText.text = dateFormat.format(history.playedAt)

            // Color code based on win/loss
            if (history.isWin) {
                card.setCardBackgroundColor(itemView.context.getColor(R.color.success))
            } else {
                card.setCardBackgroundColor(itemView.context.getColor(R.color.md_theme_light_surface))
            }
        }
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<GameHistory>() {
        override fun areItemsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem.historyId == newItem.historyId
        }

        override fun areContentsTheSame(oldItem: GameHistory, newItem: GameHistory): Boolean {
            return oldItem == newItem
        }
    }
}
