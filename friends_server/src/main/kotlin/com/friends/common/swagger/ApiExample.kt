package com.friends.common.swagger

import io.swagger.v3.oas.models.examples.Example

class ApiExample(
    val examples: Example, // 객체로 실제 예제 응답
    val code: Int, // 응답 코드
    val name: String, // 예제이름
)
