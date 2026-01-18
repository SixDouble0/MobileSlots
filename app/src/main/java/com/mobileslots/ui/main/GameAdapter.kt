package com.mobileslots.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mobileslots.R
import com.mobileslots.domain.model.Game
import com.mobileslots.domain.model.GameType

class GameAdapter(
    private val onGameClick: (Game) -> Unit
) : ListAdapter<Game, GameAdapter.GameViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_game_card, parent, false)
        return GameViewHolder(view, onGameClick)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GameViewHolder(
        itemView: View,
        private val onGameClick: (Game) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val card: MaterialCardView = itemView.findViewById(R.id.game_card)
        private val iconImageView: ImageView = itemView.findViewById(R.id.game_icon)
        private val nameTextView: TextView = itemView.findViewById(R.id.game_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.game_description)
        private val betRangeTextView: TextView = itemView.findViewById(R.id.bet_range)

        fun bind(game: Game) {
            nameTextView.text = game.name
            descriptionTextView.text = game.description
            betRangeTextView.text = itemView.context.getString(
                R.string.slot_bet_amount,
                game.minBet
            ) + " - " + game.maxBet

            iconImageView.setImageResource(
                when (game.type) {
                    GameType.SLOTS -> R.drawable.ic_slots
                    GameType.ROULETTE -> R.drawable.ic_roulette
                    GameType.BLACKJACK -> R.drawable.ic_blackjack
                }
            )

            card.setOnClickListener {
                onGameClick(game)
            }
        }
    }

    private class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
        override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem.gameId == newItem.gameId
        }

        override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean {
            return oldItem == newItem
        }
    }
}
