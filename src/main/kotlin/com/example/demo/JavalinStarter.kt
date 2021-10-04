package com.example.demo

import com.example.demo.api.ApiController
import io.javalin.Javalin
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.swagger.v3.oas.models.info.Info

object JavalinStarter {

    @JvmStatic
    fun main(args: Array<String>) {
        val port = args.getOrNull(0)?.toIntOrNull() ?: 7111

        val app = Javalin.create { config ->
            config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
        }.start(port)

        app.post("/generate", ApiController.generateMatchesHandler)
        app.get("/search", ApiController.searchMatchesHandler)
    }



    private fun getOpenApiOptions(): OpenApiOptions {
        val applicationInfo: Info = Info()
                .version("1.0")
                .description("Pattern matcher")
        return OpenApiOptions(applicationInfo)
                .path("/swagger-docs")
                .swagger(SwaggerOptions("/swagger-ui"))
    }
}
