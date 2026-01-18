package com.mobileslots.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RandomNumberResponse(
    @SerializedName("result")
    val result: RandomResult
)

data class RandomResult(
    @SerializedName("random")
    val random: RandomData
)

data class RandomData(
    @SerializedName("data")
    val data: List<Int>
)
