package com.example.demo.matching

import com.mifmif.common.regex.Generex

object TextMatcherService {

    val initialCharacters = mutableMapOf<Char, LinkedCharacter>()
    var initialized = false
        private set

    fun generate(pattern: String = "(?:[abcdef]{3}\\d){1,2}"): String {
        val input = pattern.replace("?:", "")

        if (pattern.contains("?="))
            throw RuntimeException("Positive lookahead ('?=') is not supported")
        if (pattern.contains("?!"))
            throw RuntimeException("Negative lookahead ('?!') is not supported")
        if (pattern.contains("?<="))
            throw RuntimeException("Positive lookbehind ('?!') is not supported")
        if (pattern.contains("?<!"))
            throw RuntimeException("Negative lookbehind ('?!') is not supported")


        val generex = Generex(input)
        if (generex.isInfinite)
            throw RuntimeException("Given regular expression '$input' yields infinite combinations")

        initialCharacters.clear()

        val matchIterator = generex.iterator()

        while (matchIterator.hasNext()) {
            val match = matchIterator.next()
            var curretChar = initialCharacters.computeIfAbsent(match[0]) {
                LinkedCharacter(match[0])
            }
            match.takeLast(match.length - 1).forEach {
                curretChar = curretChar.checkLink(it)
            }
            curretChar.isTerminal = true
        }
        initialized = true

        return "Number of generated results: ${generex.matchedStringsSize()} for the pattern '$pattern'"
    }


    fun listMatchedStrings(input: String): Set<String> {
        val matchedStrings = mutableSetOf<String>()

        // go over each character of the input and try to match generated links
        input.forEachIndexed { currentIndex, char ->
            val linkedCharacter = initialCharacters[char] ?: return@forEachIndexed

            linkedCharacter.getMatchingIndexes(input, currentIndex)?.forEach {
                matchedStrings.add(input.substring(currentIndex, it + 1))
            }

        }
        return matchedStrings
    }
}