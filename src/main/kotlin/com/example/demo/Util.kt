package com.example.demo

import com.mifmif.common.regex.Generex

inline fun <reified T : Any?> T.andPrint(): T {
    println(this)
    return this
}

inline fun Generex.forEachMatch(action: (String) -> Unit){
    val iterator = this.iterator()
    while (iterator.hasNext())
        action(iterator.next())
}
