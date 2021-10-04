package com.example.demo.api

import com.example.demo.dto.MatchResultResponse
import com.example.demo.dto.MessageResponse
import com.example.demo.matching.TextMatcherService
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
            OpenApiResponse(status = "200", content = [OpenApiContent(from = MessageResponse::class)]),
            OpenApiResponse(status = "400", content = [OpenApiContent(from = MessageResponse::class)]),
            OpenApiResponse(status = "500", content = [OpenApiContent(from = MessageResponse::class)])
        ]
    )
    val generateMatchesHandler = Handler { ctx ->
        ctx.processResponse {
            json(MessageResponse(TextMatcherService.generate()))
        }
    }


    @OpenApi(
        summary = "Find all matches for provided 'text' query parameter",
        queryParams = [OpenApiParam("text", String::class, "Text to be matched")],
        responses = [
            OpenApiResponse(status = "200", content = [OpenApiContent(from = MatchResultResponse::class)]),
            OpenApiResponse(status = "400", content = [OpenApiContent(from = MessageResponse::class)]),
            OpenApiResponse(status = "500", content = [OpenApiContent(from = MessageResponse::class)])
        ]
    )
    val searchMatchesHandler = Handler { ctx ->
        ctx.processResponse {
            ctx.queryParam("text")?.let { textParam ->
                val matches = TextMatcherService.listMatchedStrings(textParam)
                ctx.setResponse(MatchResultResponse("Matched ${matches.size} words", matches))
                return@Handler
            }

            ctx.setResponse("No input provided. Expected Query parameter: 'text'", 400)
        }
    }
}
