package com.example.composeapp.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

@ApiVersionDependent
fun playVibration(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Прямая проверка SDK_INT внутри метода
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        // Устаревший метод для старых версий
        @Suppress("DEPRECATION")
        vibrator.vibrate(100)
    }
}

fun x(context: Context) {
    playVibration(context)
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class ApiVersionDependent
