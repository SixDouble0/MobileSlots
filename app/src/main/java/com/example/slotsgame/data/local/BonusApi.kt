package com.example.slotsgame.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Prosta odpowiedź JSON: {"amount": 100}
data class DailyBonus(val amount: Int)

interface BonusService {
    // Używamy przykładowego mock API lub generycznego
    // Tutaj symulacja: normalnie podałbyś endpoint np. /daily-bonus
    @GET("random?min=10&max=100")
    suspend fun getDailyBonus(): DailyBonus
}

object RetrofitClient {
    // Placeholder URL - w prawdziwym projekcie użyj np. mockable.io
    private const val BASE_URL = "https://demo123.mockable.io/"

    val api: BonusService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BonusService::class.java)
    }
}