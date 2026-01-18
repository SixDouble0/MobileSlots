package com.example.slotsgame.ui.game

import android.app.Application
import androidx.lifecycle.*
import com.example.slotsgame.data.local.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).gameDao()

    // Dane obserwowane przez UI (LiveData)
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _spinResult = MutableLiveData<List<Int>>()
    val spinResult: LiveData<List<Int>> = _spinResult

    private val _winMessage = MutableLiveData<String>()
    val winMessage: LiveData<String> = _winMessage

    // 0=Diament, 1=Dzwonek, 2=Siódemka, 3=Cytryna, 4=Wiśnia
    private val symbols = listOf(0, 1, 2, 3, 4)

    fun loginOrRegister(username: String) {
        viewModelScope.launch {
            var user = dao.getUser(username)
            if (user == null) {
                user = User(username, 1000.0) // Startowy bonus
                dao.insertUser(user)
            }
            _currentUser.value = user!!
        }
    }

    fun spin(bet: Double) {
        val user = _currentUser.value ?: return
        if (user.balance < bet) {
            _winMessage.value = "Brak środków!"
            return
        }

        val r1 = symbols.random()
        val r2 = symbols.random()
        val r3 = symbols.random()

        _spinResult.value = listOf(r1, r2, r3)

        // Sprawdź wygraną w środkowej linii
        var win = 0.0
        val middleSymbol1 = r1
        val middleSymbol2 = r2
        val middleSymbol3 = r3

        if (middleSymbol1 == middleSymbol2 && middleSymbol2 == middleSymbol3) {
            // Trzy takie same symbole
            val multiplier = when (middleSymbol1) {
                0 -> 100.0 // Diament
                1 -> 50.0  // Dzwonek
                2 -> 25.0  // Siódemka
                3 -> 10.0  // Cytryna
                4 -> 5.0   // Wiśnia
                else -> 1.0
            }
            win = bet * multiplier
            _winMessage.value = "JACKPOT! +$win"
        } else if (middleSymbol1 == middleSymbol2 || middleSymbol2 == middleSymbol3) {
            // Dwa takie same symbole od lewej
            val comboSymbol = if(middleSymbol1 == middleSymbol2) middleSymbol1 else middleSymbol2
            val multiplier = when (comboSymbol) {
                0 -> 20.0 // Diament
                1 -> 10.0  // Dzwonek
                2 -> 5.0  // Siódemka
                3 -> 2.0   // Cytryna
                4 -> 1.5   // Wiśnia
                else -> 1.0
            }
            win = bet * multiplier
            _winMessage.value = "Wygrana! +$win"
        } else {
            win = 0.0
            _winMessage.value = ""
        }

        val newBalance = user.balance - bet + win
        val updatedUser = user.copy(balance = newBalance)
        _currentUser.value = updatedUser

        viewModelScope.launch {
            dao.updateUser(updatedUser)
            dao.insertSpin(SpinHistory(
                username = user.username,
                winAmount = win,
                timestamp = System.currentTimeMillis(),
                symbols = "$r1|$r2|$r3"
            ))
        }
    }
}