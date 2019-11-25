package bj4.dev.yhh.repository.converter

import androidx.room.TypeConverter
import bj4.dev.yhh.lottery_parser.LotteryRawData
import bj4.dev.yhh.repository.CellData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LotteryRawDataConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringToLotteryRawData(value: String): LotteryRawData {
        return gson.fromJson(value, LotteryRawData::class.java)
    }

    @TypeConverter
    fun fromLotteryRawDataToString(value: LotteryRawData): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToCellData(value: String): CellData {
        return gson.fromJson(value, CellData::class.java)
    }

    @TypeConverter
    fun fromCellDataToString(value: CellData): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToCellDataList(value: String): List<CellData> {
        return gson.fromJson(value, object : TypeToken<List<CellData>>() {}.type)
    }

    @TypeConverter
    fun fromCellDataListToString(value: List<CellData>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun fromStringToIntList(value: String): List<Int> {
        return gson.fromJson(value, object : TypeToken<List<Int>>() {}.type)
    }

    @TypeConverter
    fun fromIntListToString(value: List<Int>): String {
        return gson.toJson(value)
    }
}