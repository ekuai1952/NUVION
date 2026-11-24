package kz.nuvion.app

import kz.nuvion.app.util.AnswerChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerCheckerTest {

    @Test
    fun parsesDotCommaAndUnicodeMinus() {
        assertEquals(2.12, AnswerChecker.parse("2.12")!!, 1e-9)
        assertEquals(2.12, AnswerChecker.parse(" 2,12 ")!!, 1e-9)
        assertEquals(-1.51, AnswerChecker.parse("−1.51")!!, 1e-9)
        assertEquals(100000.0, AnswerChecker.parse("1e5")!!, 1e-9)
    }

    @Test
    fun rejectsGarbage() {
        assertNull(AnswerChecker.parse(""))
        assertNull(AnswerChecker.parse("abc"))
        assertNull(AnswerChecker.parse("1.2.3"))
    }

    @Test
    fun toleranceIsRelative() {
        assertTrue(AnswerChecker.isCorrect(2.12, 2.12, 0.01))
        assertTrue(AnswerChecker.isCorrect(2.13, 2.12, 0.01))
        assertFalse(AnswerChecker.isCorrect(2.2, 2.12, 0.01))
        assertTrue(AnswerChecker.isCorrect(1836.0, 1833.0, 0.01))
    }

    @Test
    fun formatsWholeNumbersWithoutDecimals() {
        assertEquals("125", AnswerChecker.format(125.0))
        assertEquals("2.12", AnswerChecker.format(2.12))
    }
}
