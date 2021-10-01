package com.example.demo.matching

class LinkedCharacter(val character: Char, var next: LinkedCharacter? = null
) {
    val nextLinked = mutableMapOf<Char, LinkedCharacter>()
    var isTerminal = false

    fun checkLink(chr: Char) = nextLinked.computeIfAbsent(chr) { LinkedCharacter(chr) }

    fun getMatchingIndexes(text: String, startIdx: Int): List<Int>? {
        var result: MutableList<Int>? = null
        var idx = startIdx
        var currentChar: LinkedCharacter = this

        while (text.length > ++idx) {
            val next = currentChar.nextLinked[text[idx]] ?: return result
            if (next.isTerminal) {
                if (result == null)
                    result = mutableListOf<Int>()
                result.add(idx)
            }
            currentChar = next
        }
        return result
    }
}