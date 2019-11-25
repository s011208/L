package bj4.dev.yhh.lottery_parser.lto

import bj4.dev.yhh.lottery_parser.LotteryParser
import bj4.dev.yhh.lottery_parser.LotteryRawData
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LtoParser : LotteryParser {
    companion object {
        private const val URL = "https://www.pilio.idv.tw/lto/list.asp?indexpage="

        private const val COLUMN_COUNT = 3

        private const val DATE_FORMATTER = "yyyy MM/dd"
    }

    private val dateFormat = SimpleDateFormat(DATE_FORMATTER, Locale.getDefault())

    override fun parse(page: Int): List<LotteryRawData> {
        require(page > 0) { "page should >= 1" }

        val doc = Jsoup.connect("$URL$page").get()
        val elementTable = doc.select("table.auto-style1")
        val tds = elementTable.select("td")

        val rtn = ArrayList<LotteryRawData>()

        var date: Long = 0
        var numberList: List<Int> = ArrayList()
        var specialNumberList: List<Int>
        for (i in COLUMN_COUNT until tds.size) {
            val value = tds[i].text()
            when {
                i % COLUMN_COUNT == 0 -> date = dateConverter(value)
                i % COLUMN_COUNT == 1 -> numberList = numberConverter(value)
                i % COLUMN_COUNT == 2 -> {
                    specialNumberList = specialNumberConverter(value)
                    rtn.add(
                        LotteryRawData(
                            date,
                            numberList,
                            specialNumberList
                        )
                    )
                }
            }
        }

        for (item in rtn) {
            println(item)
        }

        return rtn
    }

    private fun dateConverter(date: String): Long {
        return dateFormat.parse(date.substring(0, date.length - 3).trim())?.time ?: 0L
    }

    private fun numberConverter(numbers: String): List<Int> {
        return numbers.split(",").map { it.trim().toInt() }.toList()
    }

    private fun specialNumberConverter(number: String): List<Int> {
        return ArrayList<Int>().apply { add(number.toInt()) }
    }
}