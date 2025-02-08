package com.friends.common.annotation

import com.friends.common.exception.ErrorCode
import com.friends.common.exception.ParameterValidationException
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Constraint(validatedBy = [CustomPositiveValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomPositive(
    val message: String = "채팅방 ID는 양수여야 합니다.",
    val errorCode: ErrorCode = ErrorCode.INVALID_CHAT_ROOM_ID,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class CustomPositiveValidator : ConstraintValidator<CustomPositive, Number> {
    private lateinit var errorCode: ErrorCode

    override fun initialize(constraintAnnotation: CustomPositive) {
        this.errorCode = constraintAnnotation.errorCode
    }

    override fun isValid(
        value: Number,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.toLong() > 0) {
            return true
        }
        throw ParameterValidationException(errorCode)
    }
}
