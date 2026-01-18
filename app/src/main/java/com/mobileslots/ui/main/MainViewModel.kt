package com.mobileslots.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobileslots.data.repository.AchievementRepository
import com.mobileslots.data.repository.GameRepository
import com.mobileslots.data.repository.UserRepository
import com.mobileslots.domain.model.Game
import com.mobileslots.domain.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Game>>(emptyList())
    val searchResults: StateFlow<List<Game>> = _searchResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCurrentUser()
        loadGames()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
            }
        }
    }

    private fun loadGames() {
        viewModelScope.launch {
            gameRepository.getActiveGames().collect { gameList ->
                _games.value = gameList
                if (_searchResults.value.isEmpty()) {
                    _searchResults.value = gameList
                }
            }
        }
    }

    fun initializeApp() {
        viewModelScope.launch {
            try {
                // Check if user exists, create default user if not
                val userCount = userRepository.getUserCount()
                if (userCount == 0) {
                    userRepository.createUser("Player")
                }

                // Initialize games if not present
                if (_games.value.isEmpty()) {
                    gameRepository.initializeGames()
                }

                // Initialize achievements
                achievementRepository.initializeAchievements()
            } catch (e: Exception) {
                _error.value = "Failed to initialize app: ${e.message}"
            }
        }
    }

    fun searchGames(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _searchResults.value = _games.value
            } else {
                gameRepository.searchGames(query).collect { results ->
                    _searchResults.value = results
                }
            }
        }
    }
}

class MainViewModelFactory(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val achievementRepository: AchievementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(userRepository, gameRepository, achievementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
