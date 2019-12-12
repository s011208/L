package bj4.dev.yhh.lottery_parser.lto_hk

import bj4.dev.yhh.lottery_parser.LotteryParser
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*

class LtoHKParserTest {

    private lateinit var parser: LotteryParser

    @Before
    fun setUp() {
        parser = LtoHKParser()
    }

    @Test
    fun parse() {
        assertEquals(parser.parse(3).size, 23)
    }
}