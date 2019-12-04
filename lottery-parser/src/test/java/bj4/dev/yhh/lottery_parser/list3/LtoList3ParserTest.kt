package bj4.dev.yhh.lottery_parser.list3

import bj4.dev.yhh.lottery_parser.LotteryParser
import bj4.dev.yhh.lottery_parser.lto.LtoParser
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class LtoList3ParserTest {

    private lateinit var parser: LotteryParser

    @Before
    fun setUp() {
        parser = LtoList3Parser()
    }

    @Test
    fun parse() {
        assertEquals(parser.parse(1).size, 50)
    }
}