package com.friends.common.annotation

import com.friends.common.exception.ErrorCode
import com.friends.common.exception.ParameterValidationException
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [NullOrNotBlankValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class NullOrNotBlank(
    val errorCode: ErrorCode = ErrorCode.INVALID_REQUEST,
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class NullOrNotBlankValidator : ConstraintValidator<NullOrNotBlank, String?> {
    private lateinit var errorCode: ErrorCode

    override fun initialize(contactNumber: NullOrNotBlank) {
        this.errorCode = contactNumber.errorCode
    }

    override fun isValid(
        contactField: String?,
        cxt: ConstraintValidatorContext?,
    ): Boolean {
        if (contactField == null || contactField.isNotBlank()) {
            return true
        }
        throw ParameterValidationException(errorCode)
    }
}
