package com.example.stylecraft.core

import kotlin.random.Random

object OtpManager {
    private var currentOtp: String = ""

    fun generateOtp(): String {
        currentOtp = (Random.nextInt(900000) + 100000).toString()
        return currentOtp
    }

    fun verifyOtp(input: String): Boolean {
        return input == currentOtp
    }

    fun getOtp(): String = currentOtp
}
