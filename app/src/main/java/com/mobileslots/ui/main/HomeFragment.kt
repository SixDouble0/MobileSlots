package com.mobileslots.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.AchievementRepository
import com.mobileslots.data.repository.GameRepository
import com.mobileslots.data.repository.UserRepository
import com.mobileslots.domain.model.GameType
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var gameAdapter: GameAdapter
    private lateinit var balanceTextView: MaterialTextView
    private lateinit var tutorialButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupViews(view)
        observeViewModel()
    }

    private fun setupViewModel() {
        val database = (requireActivity().application as MobileSlotsApplication).database
        val userRepository = UserRepository(database.userDao(), database.userSettingsDao())
        val gameRepository = GameRepository(database.gameDao())
        val achievementRepository = AchievementRepository(
            database.achievementDao(),
            database.userAchievementDao()
        )

        val factory = MainViewModelFactory(userRepository, gameRepository, achievementRepository)
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]
    }

    private fun setupViews(view: View) {
        balanceTextView = view.findViewById(R.id.balance_text_view)
        tutorialButton = view.findViewById(R.id.tutorial_button)

        val recyclerView: RecyclerView = view.findViewById(R.id.games_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        gameAdapter = GameAdapter { game ->
            when (game.type) {
                GameType.SLOTS -> findNavController().navigate(R.id.action_home_to_slots)
                GameType.ROULETTE -> findNavController().navigate(R.id.action_home_to_roulette)
                GameType.BLACKJACK -> findNavController().navigate(R.id.action_home_to_blackjack)
            }
        }

        recyclerView.adapter = gameAdapter

        tutorialButton.setOnClickListener {
            (requireActivity() as MainActivity).openYouTubeTutorial()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                user?.let {
                    balanceTextView.text = getString(R.string.profile_balance, it.balance)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.games.collect { games ->
                gameAdapter.submitList(games)
            }
        }
    }
}
