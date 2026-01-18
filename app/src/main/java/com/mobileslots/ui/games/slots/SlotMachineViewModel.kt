package com.mobileslots.ui.games.slots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobileslots.data.repository.GameHistoryRepository
import com.mobileslots.data.repository.UserRepository
import com.mobileslots.domain.model.GameHistory
import com.mobileslots.domain.model.GameType
import com.mobileslots.domain.model.User
import com.mobileslots.domain.model.UserSettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

data class SlotMachineResult(
    val symbols: List<Int>,
    val isWin: Boolean,
    val winAmount: Int
)

class SlotMachineViewModel(
    private val userRepository: UserRepository,
    private val gameHistoryRepository: GameHistoryRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userSettings = MutableStateFlow<UserSettings?>(null)
    val userSettings: StateFlow<UserSettings?> = _userSettings.asStateFlow()

    private val _betAmount = MutableStateFlow(10)
    val betAmount: StateFlow<Int> = _betAmount.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _spinResult = MutableStateFlow<SlotMachineResult?>(null)
    val spinResult: StateFlow<SlotMachineResult?> = _spinResult.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
                user?.let {
                    loadUserSettings(it.userId)
                }
            }
        }
    }

    private fun loadUserSettings(userId: Long) {
        viewModelScope.launch {
            userRepository.getUserSettings(userId).collect { settings ->
                _userSettings.value = settings
            }
        }
    }

    fun setBetAmount(amount: Int) {
        _betAmount.value = amount
    }

    fun spin(onSymbolsGenerated: (List<Int>) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            _error.value = "User not found"
            return
        }

        val bet = _betAmount.value
        if (bet > user.balance) {
            _error.value = "Insufficient balance"
            return
        }

        if (_isSpinning.value) {
            return
        }

        _isSpinning.value = true

        viewModelScope.launch {
            try {
                // Generate random symbols (0-4 for 5 different symbols)
                val symbols = List(3) { Random.nextInt(0, 5) }
                onSymbolsGenerated(symbols)

                // Calculate win
                val result = calculateWin(symbols, bet)
                _spinResult.value = result

                // Update balance
                val newBalance = user.balance - bet + result.winAmount
                userRepository.updateBalance(user.userId, newBalance)
                userRepository.updateLastPlayed(user.userId)

                // Save history
                saveGameHistory(user.userId, bet, result.winAmount, newBalance)

                _isSpinning.value = false
            } catch (e: Exception) {
                _error.value = "Error during spin: ${e.message}"
                _isSpinning.value = false
            }
        }
    }

    private fun calculateWin(symbols: List<Int>, bet: Int): SlotMachineResult {
        val isWin = when {
            // All three symbols match
            symbols[0] == symbols[1] && symbols[1] == symbols[2] -> {
                when (symbols[0]) {
                    4 -> 100 // Diamond - highest payout
                    3 -> 50  // Seven
                    2 -> 25  // Bell
                    1 -> 10  // Lemon
                    else -> 5 // Cherry
                }
            }
            // Two symbols match
            symbols[0] == symbols[1] || symbols[1] == symbols[2] || symbols[0] == symbols[2] -> {
                2
            }
            else -> 0
        }

        val winAmount = bet * isWin
        return SlotMachineResult(
            symbols = symbols,
            isWin = winAmount > 0,
            winAmount = winAmount
        )
    }

    private suspend fun saveGameHistory(
        userId: Long,
        betAmount: Int,
        winAmount: Int,
        balanceAfter: Int
    ) {
        val history = GameHistory(
            userId = userId,
            gameId = 1, // Slot machine game ID
            gameType = GameType.SLOTS,
            betAmount = betAmount,
            winAmount = winAmount,
            balanceAfter = balanceAfter,
            playedAt = Date()
        )
        gameHistoryRepository.addGameHistory(history)
    }
}

class SlotMachineViewModelFactory(
    private val userRepository: UserRepository,
    private val gameHistoryRepository: GameHistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SlotMachineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SlotMachineViewModel(userRepository, gameHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
