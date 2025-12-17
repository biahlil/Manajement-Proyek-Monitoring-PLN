package com.pln.monitoringpln.domain.usecase.validation

enum class ValidationType {
    STRICT, // Only Alphanumeric + Space
    TEXT, // Alphanumeric + Space + Dot + Comma
}

class ValidateInputUseCase {

    operator fun invoke(input: String, type: ValidationType = ValidationType.TEXT): ValidationResult {
        val regex = when (type) {
            ValidationType.STRICT -> Regex("^[a-zA-Z0-9 ]*$")
            ValidationType.TEXT -> Regex("^[a-zA-Z0-9 .,]*$")
        }

        if (!regex.matches(input)) {
            val message = when (type) {
                ValidationType.STRICT -> "Format tidak valid: Simbol tidak diperbolehkan."
                ValidationType.TEXT -> "Format tidak valid: Hanya huruf, angka, spasi, titik, dan koma yang diperbolehkan."
            }
            return ValidationResult(
                successful = false,
                errorMessage = message,
            )
        }

        return ValidationResult(successful = true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null,
)
