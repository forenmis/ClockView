package com.example.clockview.entity

sealed class Weather(open val temperature: Int) {
    data class Sunny(override val temperature: Int) : Weather(temperature)
    data class Cloud(override val temperature: Int) : Weather(temperature)
    data class Snow(override val temperature: Int) : Weather(temperature)
    data class Wind(override val temperature: Int) : Weather(temperature)
}
