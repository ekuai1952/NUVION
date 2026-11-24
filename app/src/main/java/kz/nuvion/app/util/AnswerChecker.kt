package kz.nuvion.app.util

import kotlin.math.abs
import kotlin.math.max

object AnswerChecker {

    /** Accepts "2.12", "2,12", " 2.12 ", "1e5", "−1.51" (Unicode minus). Returns null if not a number. */
    fun parse(input: String): Double? {
        val normalized = input.trim()
            .replace(" ", "")
            .replace(' '.toString(), "")
            .replace(',', '.')
            .replace('−', '-')
        if (normalized.isEmpty()) return null
        return normalized.toDoubleOrNull()?.takeIf { it.isFinite() }
    }

    fun isCorrect(value: Double, expected: Double, relativeTolerance: Double): Boolean {
        val allowed = max(abs(expected) * relativeTolerance, 1e-9)
        return abs(value - expected) <= allowed
    }

    /** Formats an expected answer without a trailing ".0". */
    fun format(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()
}
