package com.example.demo.api

import com.example.demo.dto.MessageResponse
import io.javalin.http.Context

inline fun Context.processResponse(status: Int = 200, responseProcess: Context.() -> Unit) {
    try {
        status(status)
        responseProcess()
    } catch (th: Throwable) {
        val errorStatus = if (th is BadRequestException) 400 else 500
        setResponse(th.localizedMessage, errorStatus)
    }
}

fun Context.setResponse(message: String, status: Int = 200) = setResponse(MessageResponse(message), status)

fun Context.setResponse(body: Any, status: Int = 200) {
    status(status)
    json(body)
}

