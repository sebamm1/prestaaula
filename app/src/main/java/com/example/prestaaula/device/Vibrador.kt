package com.example.prestaaula.device

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * RECURSO DEL DISPOSITIVO 1: vibración (rúbrica IE 2.4.1).
 * Requiere el permiso VIBRATE en el AndroidManifest (permiso normal: no se le pregunta al usuario).
 * El emulador no vibra; por eso la app además muestra un aviso visual "Vibración".
 * Nunca hace caer la app: si el dispositivo no puede vibrar, no pasa nada.
 */
object Vibrador {
    fun vibrar(context: Context, milisegundos: Long = 200) {
        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(milisegundos, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (e: Exception) {
            // Sin vibración disponible: la app sigue funcionando igual.
        }
    }
}
