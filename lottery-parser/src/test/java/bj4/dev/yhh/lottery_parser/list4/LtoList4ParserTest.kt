package bj4.dev.yhh.lottery_parser.list4

import bj4.dev.yhh.lottery_parser.LotteryParser
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LtoList4ParserTest{

    private lateinit var parser: LotteryParser

    @Before
    fun setUp() {
        parser = LtoList4Parser()
    }

    @Test
    fun parse() {
        assertEquals(parser.parse(1).size, 50)
    }
}