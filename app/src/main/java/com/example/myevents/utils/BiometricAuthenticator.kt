package com.example.myevents.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import com.example.myevents.R

class BiometricAuthenticator (
    private val context: Context
) {
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val biometricManager = BiometricManager.from(context)
    private lateinit var biometricPrompt: BiometricPrompt

    fun isAvailable(): BiometricAuthStatus {
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED
            else -> BiometricAuthStatus.NOT_AVAILABLE
        }
    }

    fun promptBiometricAuthentication(
        title: String,
        subtitle: String,
        fragmentActivity: FragmentActivity,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
        onFailed: () -> Unit,
        onError: (error: Int, message: String) -> Unit
    ) {
        when (isAvailable()) {
            BiometricAuthStatus.NOT_AVAILABLE -> {
                onError(BiometricAuthStatus.NOT_AVAILABLE.id, context.getString(R.string.bio_not_av))
            }
            BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE -> {
                onError(BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE.id, context.getString(R.string.bio_temp_not))
            }
            BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                onError(BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED.id, context.getString(R.string.bio_not_roll))
            }
            else -> Unit
        }
        biometricPrompt = BiometricPrompt(
            fragmentActivity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }
            }
        )
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}