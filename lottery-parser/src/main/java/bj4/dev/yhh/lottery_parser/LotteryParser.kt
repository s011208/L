package bj4.dev.yhh.lottery_parser

import io.reactivex.Single

interface LotteryParser {
    fun getDefaultUrl(): String
    fun getUrl(): Single<String>

    fun parseAsync(page: Int = 1): Single<List<LotteryRawData>>
    fun parse(page: Int = 1): List<LotteryRawData>
    fun parseWithUrl(url: String, page: Int = 1): List<LotteryRawData>
}