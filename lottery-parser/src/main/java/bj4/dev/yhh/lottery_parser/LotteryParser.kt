package bj4.dev.yhh.lottery_parser

interface LotteryParser {
    fun parse(page: Int = 1): List<LotteryRawData>
}