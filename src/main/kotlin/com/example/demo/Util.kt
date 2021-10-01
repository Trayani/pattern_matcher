package com.example.demo

inline fun <reified T : Any?> T.andPrint(): T {
    println(this)
    return this
}
