package bj4.dev.yhh.lottery_parser.lto

import bj4.dev.yhh.lottery_parser.LotteryParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LtoParserTest {
    private lateinit var parser: LotteryParser

    @Before
    fun setUp() {
        parser = LtoParser()
    }

    @Test
    fun parse() {
        assertEquals(parser.parse(3).size, 23)
    }
}