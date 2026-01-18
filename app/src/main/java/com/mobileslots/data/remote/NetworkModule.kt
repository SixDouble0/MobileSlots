package com.mobileslots.data.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobileslots.data.remote.api.GameApiService
import com.mobileslots.data.remote.api.RandomOrgApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
    }

    fun provideGameApiService(): GameApiService {
        return provideRetrofit(GameApiService.BASE_URL).create(GameApiService::class.java)
    }

    fun provideRandomOrgApiService(): RandomOrgApiService {
        return provideRetrofit(RandomOrgApiService.BASE_URL).create(RandomOrgApiService::class.java)
    }
}
