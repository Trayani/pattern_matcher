package com.example

import com.example.demo.andPrint
import com.example.demo.matching.TextMatcherService
import com.mifmif.common.regex.Generex
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

const val REPEAT_COUNT = 10

class Tests {

    val pattern = "([abcdef]{3}\\d){10,15}"
    init {
        TextMatcherService.generate()
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `matching method yields the same result as contains-check`() {
        val sample = Generex(pattern).random()
        val testResult = TextMatcherService.listMatchedStrings(sample);

        val containsResult = Generex(TextMatcherService.defaultInputPattern).allMatchedStrings
            .filter { sample.contains(it) }
            .toSet()

        assert(testResult.containsAll(containsResult))
        assert(containsResult.containsAll(testResult))
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `matching methods return consistent matches`() {

        val sample = Generex(pattern).random()

        val parallelResult = runMatchingMethod("Parallel", sample) {
            TextMatcherService.listMatchedStringsParallel(it)
        }
        val sequentialResult = runMatchingMethod("Sequential", sample) {
            TextMatcherService.listMatchedStrings(it)
        }
        val coroutinesResult = runMatchingMethod("Coroutines", sample) {
            TextMatcherService.listMatchedStringsByCoroutines(it)
        }

        assert(parallelResult.containsAll(sequentialResult))
        assert(sequentialResult.containsAll(parallelResult))

        assert(parallelResult.containsAll(coroutinesResult))
        assert(coroutinesResult.containsAll(parallelResult))
    }


    inline fun runMatchingMethod(matchingType: String, input: String, matchindMethod: (String) -> Set<String>)
            : Set<String> {
        val start = System.currentTimeMillis()
        val result = matchindMethod(input)
        val finish = System.currentTimeMillis()
        val timeElapsed = finish - start
        "$matchingType processing time: $timeElapsed ms".andPrint()
        return result
    }

}
