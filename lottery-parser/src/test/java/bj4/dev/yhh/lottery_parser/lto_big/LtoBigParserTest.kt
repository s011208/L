package bj4.dev.yhh.lottery_parser.lto_big

import bj4.dev.yhh.lottery_parser.LotteryParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LtoBigParserTest {
    private lateinit var parser: LotteryParser

    @Before
    fun setUp() {
        parser = LtoBigParser()
    }

    @Test
    fun parse() {
        assertEquals(parser.parse(999).size, 23)
    }
}