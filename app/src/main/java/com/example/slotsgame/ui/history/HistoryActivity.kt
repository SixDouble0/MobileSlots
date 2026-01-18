package com.example.slotsgame.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.slotsgame.data.local.AppDatabase
import com.example.slotsgame.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadData()

        // Obsługa strzałki "wstecz" na pasku
        binding.historyToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun loadData() {
        val username = intent.getStringExtra("USERNAME") ?: return
        val dao = AppDatabase.getDatabase(this).gameDao()

        // Pobieranie danych z bazy w tle (Coroutines Flow)
        lifecycleScope.launch {
            dao.getHistoryFlow(username).collect { historyList ->
                adapter.submitList(historyList)
            }
        }
    }
}