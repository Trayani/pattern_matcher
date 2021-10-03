package com.example.demo

import com.example.demo.dto.MatchResultResponseDto
import com.example.demo.dto.ResponseDto
import com.example.demo.matching.TextMatcherService
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiParam
import io.javalin.plugin.openapi.annotations.OpenApiResponse

object ApiController {

    @OpenApi(
            summary = "Generate string combination matching '(?:[abcdef]{3}\\d){1,2}' pattern. " +
                    "For the sake of memory safety, the pattern is fixed",
            responses = [
                OpenApiResponse(status = "200", content = [OpenApiContent(from = ResponseDto::class)]),
                OpenApiResponse(status = "500", content = [OpenApiContent(from = ResponseDto::class)])
            ])
    val generateMatchesHandler = Handler { ctx ->
        try {
            ctx.setResponse(200, TextMatcherService.generate())
        } catch (th: Throwable) {
            ctx.setResponse(500, th.localizedMessage)
        }
    }

    @OpenApi(
            summary = "Find all matches for provided 'text' query parameter",
            queryParams = [OpenApiParam("text", String::class, "Text to be matched")],
            responses = [
                OpenApiResponse(status = "200", content = [OpenApiContent(from = MatchResultResponseDto::class)]),
                OpenApiResponse(status = "400", content = [OpenApiContent(from = ResponseDto::class)])
            ])
    val seachMatchesHandler = Handler { ctx ->
        val input = ctx.queryParam("text")
        if (input == null)
            ctx.setResponse(400, "No input provided. Expected Query parameter 'text'")
        else {
            try {
                val matches = TextMatcherService.listMatchedStrings(input)
                ctx.status(200)
                ctx.json(MatchResultResponseDto("Matched ${matches.size} words", matches))
            } catch (th: Throwable) {
                ctx.setResponse(500, th.localizedMessage)
            }
        }
    }


    fun Context.setResponse(status: Int, message: String) {
        status(status)
        json(ResponseDto(message))
    }
}