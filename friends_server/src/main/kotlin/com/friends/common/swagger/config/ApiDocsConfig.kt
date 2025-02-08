package com.friends.common.swagger.config

import com.friends.common.exception.ErrorCode
import com.friends.common.exception.ErrorResponse
import com.friends.common.swagger.ApiErrorCodeExamples
import com.friends.common.swagger.ApiExample
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.method.HandlerMethod

@Configuration
class ApiDocsConfig {
    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Friends API")
                    .description("Friends API 문서입니다.")
                    .version("1.0.0"),
            ).components(createSecurityComponents())
            .addSecurityItem(createSecurityRequirement())

    private fun createSecurityComponents(): Components =
        Components()
            .addSecuritySchemes(
                AUTHORIZATION,
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .`in`(SecurityScheme.In.HEADER)
                    .name(AUTHORIZATION),
            )

    private fun createSecurityRequirement(): SecurityRequirement =
        SecurityRequirement()
            .addList(AUTHORIZATION)

    @Bean
    fun customize(): OperationCustomizer =
        OperationCustomizer { operation: Operation, handlerMethod: HandlerMethod ->
            val apiErrorCodeExamples =
                handlerMethod.getMethodAnnotation(ApiErrorCodeExamples::class.java)

            apiErrorCodeExamples?.run {
                generateErrorCodeResponseExample(operation, apiErrorCodeExamples.value)
            }
            operation
        }

    // apiErrorCodeExamples 에서 받은 ErrorCode 배열을 통해 ErrorResponse Example을 생성
    private fun generateErrorCodeResponseExample(
        operation: Operation,
        errorCodes: Array<ErrorCode>,
    ) {
        val responses = operation.responses

        val statusWithApiExampleHolders =
            errorCodes
                .map { errorCode ->
                    ApiExample(
                        examples = getSwaggerExample(errorCode),
                        code = errorCode.httpStatus.value(),
                        name = errorCode.name,
                    )
                }.groupBy { it.code }

        addExamplesToResponses(responses, statusWithApiExampleHolders)
    }

    private fun getSwaggerExample(errorCode: ErrorCode): Example { // 이 메소드는 ErrorResponse를 Example로 변환하는 역할을 한다.
        val errorResponseDto = ErrorResponse.of(errorCode, errorCode.errorMessage)
        val example = Example()
        example.value = errorResponseDto
        return example
    }

    private fun addExamplesToResponses(
        // 이 메소드는 ApiExample을 responses에 추가하는 역할을 한다.
        responses: ApiResponses,
        statusWithApiExampleHolders: Map<Int, List<ApiExample>>,
    ) {
        statusWithApiExampleHolders.forEach { (status, v) ->
            val apiResponse = responses.getOrDefault(status.toString(), ApiResponse())
            val content = apiResponse.content ?: Content()
            val mediaType = content.getOrDefault("application/json", MediaType())

            v.forEach { apiExample ->
                mediaType.addExamples(apiExample.name, apiExample.examples)
            }
            content.addMediaType("application/json", mediaType)
            apiResponse.content = content
            responses.addApiResponse(status.toString(), apiResponse)
        }
    }
}
