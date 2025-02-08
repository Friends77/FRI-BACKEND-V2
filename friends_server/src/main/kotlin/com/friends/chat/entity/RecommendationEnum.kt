package com.friends.chat.entity

enum class AgeRangeEnum(
    val value: Int,
) {
    TEEN(10),
    TWENTY(20),
    THIRTY(30),
    FORTY(40),
    FIFTY(50),
    ABOVE_SIXTY(60),
    UNKNOWN(0),
}

enum class GenderRatioEnum {
    MAJORITY_MAN,
    MAJORITY_WOMAN,
    EQUAL,
    UNKNOWN,
}
