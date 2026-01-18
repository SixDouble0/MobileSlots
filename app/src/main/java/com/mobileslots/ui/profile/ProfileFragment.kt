package com.mobileslots.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android:view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textview.MaterialTextView
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.GameHistoryRepository
import com.mobileslots.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var userRepository: UserRepository
    private lateinit var gameHistoryRepository: GameHistoryRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = (requireActivity().application as MobileSlotsApplication).database
        userRepository = UserRepository(database.userDao(), database.userSettingsDao())
        gameHistoryRepository = GameHistoryRepository(database.gameHistoryDao())

        setupViews(view)
    }

    private fun setupViews(view: View) {
        val usernameText: MaterialTextView = view.findViewById(R.id.username_text)
        val balanceText: MaterialTextView = view.findViewById(R.id.balance_text)
        val gamesPlayedText: MaterialTextView = view.findViewById(R.id.games_played_text)
        val winsText: MaterialTextView = view.findViewById(R.id.wins_text)
        val lossesText: MaterialTextView = view.findViewById(R.id.losses_text)

        lifecycleScope.launch {
            userRepository.getCurrentUser().collect { user ->
                user?.let {
                    usernameText.text = it.username
                    balanceText.text = getString(R.string.profile_balance, it.balance)

                    val stats = gameHistoryRepository.getUserStats(it.userId)
                    gamesPlayedText.text = getString(R.string.profile_games_played, stats.totalGamesPlayed)
                    winsText.text = getString(R.string.profile_total_wins, stats.totalWins)
                    lossesText.text = getString(R.string.profile_total_losses, stats.totalLosses)
                }
            }
        }
    }
}
