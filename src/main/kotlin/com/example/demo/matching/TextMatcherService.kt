package com.example.demo.matching

import com.example.demo.forEachMatch
import com.example.demo.api.BadRequestException
import com.mifmif.common.regex.Generex
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object TextMatcherService {

    val defaultInputPattern = "([abcdef]{3}\\d){1,2}"
    private val initialCharacters = mutableMapOf<Char, LinkedCharacter>()

    var initialized = false
        private set


    fun generate(pattern: String = defaultInputPattern): String {
        val input = pattern.replace("?:", "")

        when {
            pattern.contains("?=") -> throw BadRequestException("Positive lookahead ('?=') is not supported")
            pattern.contains("?!") -> throw BadRequestException("Negative lookahead ('?!') is not supported")
            pattern.contains("?<=") -> throw BadRequestException("Positive lookbehind ('?!') is not supported")
            pattern.contains("?<!") -> throw BadRequestException("Negative lookbehind ('?!') is not supported")
        }


        val generex = Generex(input)
        if (generex.isInfinite)
            throw BadRequestException("Given regular expression '$input' yields infinite combinations")

        synchronized(this) {
            generateInternal(generex)
        }
        return "Number of generated results: ${generex.matchedStringsSize()} for the pattern '$pattern'"
    }



    private fun generateInternal(generex: Generex) {
        initialCharacters.clear()

        generex.forEachMatch { match ->
            var curretChar = initialCharacters.computeIfAbsent(match[0]) {
                LinkedCharacter(match[0])
            }

            match.takeLast(match.length - 1).forEach {
                curretChar = curretChar.checkLink(it)
            }
            curretChar.isTerminal = true
        }
        initialized = true
    }


    private fun checkInitialized() {
        !initialized && throw BadRequestException(
            "Text pattern has not been analyzed yet. Please invoke /generate request first.")
    }


    fun listMatchedStrings(input: String): Set<String> {
        checkInitialized()
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


    fun listMatchedStringsParallel(input: String): Set<String> {

        checkInitialized()
        val matchedStrings = mutableSetOf<String>()
        IntRange(0, input.length - 1).toList().parallelStream().forEach { idx ->
            initialCharacters[input[idx]]?.getMatchingIndexes(input, idx)?.forEach { endIdx ->
                val match = input.substring(idx, endIdx + 1)

                synchronized(matchedStrings) {
                    matchedStrings.add(match)
                }
            }
        }
        return matchedStrings
    }


    fun listMatchedStringsByCoroutines(input: String): Set<String> {
        checkInitialized()
        val matchedStrings = mutableSetOf<String>()

        runBlocking {
            input.forEachIndexed { currentIndex, char ->
                launch {
                    val matches = initialCharacters[char]?.getMatchingIndexes(input, currentIndex)
                    matches?.forEach { endIdx ->
                        val match = input.substring(currentIndex, endIdx + 1)

                        synchronized(matchedStrings) {
                            matchedStrings.add(match)
                        }
                    }
                }
            }
        }
        return matchedStrings
    }
}
