package com.mobileslots.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobileslots.MobileSlotsApplication
import com.mobileslots.R
import com.mobileslots.data.repository.GameHistoryRepository
import com.mobileslots.data.repository.UserRepository
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = (requireActivity().application as MobileSlotsApplication).database
        val userRepository = UserRepository(database.userDao(), database.userSettingsDao())
        val gameHistoryRepository = GameHistoryRepository(database.gameHistoryDao())

        val recyclerView: RecyclerView = view.findViewById(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        lifecycleScope.launch {
            userRepository.getCurrentUser().collect { user ->
                user?.let {
                    gameHistoryRepository.getRecentHistory(it.userId, 50).collect { history ->
                        historyAdapter.submitList(history)
                    }
                }
            }
        }
    }
}
