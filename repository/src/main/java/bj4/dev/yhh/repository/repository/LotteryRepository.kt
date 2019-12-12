package bj4.dev.yhh.repository.repository

import android.content.Context
import android.util.SparseArray
import androidx.core.util.contains
import bj4.dev.yhh.lottery_parser.LotteryParser
import bj4.dev.yhh.lottery_parser.list3.LtoList3Parser
import bj4.dev.yhh.lottery_parser.list4.LtoList4Parser
import bj4.dev.yhh.lottery_parser.lto.LtoParser
import bj4.dev.yhh.lottery_parser.lto_big.LtoBigParser
import bj4.dev.yhh.lottery_parser.lto_hk.LtoHKParser
import bj4.dev.yhh.repository.CellData
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.FirestoreHelper
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.database.LotteryDatabaseHelper
import bj4.dev.yhh.repository.entity.*
import bj4.dev.yhh.repository.services.FirestoreService
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class LotteryRepository(
    private val lotteryDatabaseHelper: LotteryDatabaseHelper,
    private val context: Context,
    private val firestoreHelper: FirestoreHelper
) {

    private val parserMap = SparseArray<LotteryParser>()

    fun getLtoHKLiveData() = lotteryDatabaseHelper.database.getLtoHKDao().queryLiveData()
    fun getLtoLiveData() = lotteryDatabaseHelper.database.getLtoDao().queryLiveData()
    fun getLtoBigLiveData() = lotteryDatabaseHelper.database.getLtoBigDao().queryLiveData()
    fun getLtoList3LiveData() = lotteryDatabaseHelper.database.getLtoList3Dao().queryLiveData()
    fun getLtoList4LiveData() = lotteryDatabaseHelper.database.getLtoList4Dao().queryLiveData()

    fun getLtoHK() = lotteryDatabaseHelper.database.getLtoHKDao().query()
    fun getLto() = lotteryDatabaseHelper.database.getLtoDao().query()
    fun getLtoBig() = lotteryDatabaseHelper.database.getLtoBigDao().query()
    fun getLtoList3() = lotteryDatabaseHelper.database.getLtoList3Dao().query()
    fun getLtoList4() = lotteryDatabaseHelper.database.getLtoList4Dao().query()

    fun nukeLtoHK() = lotteryDatabaseHelper.database.getLtoHKDao().nukeTable()
    fun nukeLto() = lotteryDatabaseHelper.database.getLtoDao().nukeTable()
    fun nukeLtoBig() = lotteryDatabaseHelper.database.getLtoBigDao().nukeTable()
    fun nukeLtoList3() = lotteryDatabaseHelper.database.getLtoList3Dao().nukeTable()
    fun nukeLtoList4() = lotteryDatabaseHelper.database.getLtoList4Dao().nukeTable()
    fun nukeResult() = lotteryDatabaseHelper.database.getResultDao().nukeTable()

    private fun getLotteryDataSingle(@LotteryType lotteryType: Int): Single<List<LotteryEntity>> =
        when (lotteryType) {
            LotteryType.LtoBig -> getLtoBig().map {
                return@map ArrayList<LotteryEntity>(it)
            }
            LotteryType.Lto -> getLto().map {
                return@map ArrayList<LotteryEntity>(it)
            }
            LotteryType.LtoHK -> getLtoHK().map {
                return@map ArrayList<LotteryEntity>(it)
            }
            LotteryType.LtoList3 -> getLtoList3().map {
                return@map ArrayList<LotteryEntity>(it)
            }
            LotteryType.LtoList4 -> getLtoList4().map {
                return@map ArrayList<LotteryEntity>(it)
            }
            else -> throw IllegalArgumentException("Wrong lottery type")
        }

    fun parseLotteryData(@LotteryType lotteryType: Int): Observable<Int> {
        val parser =
            if (parserMap.contains(lotteryType)) {
                parserMap.get(lotteryType)
            } else {
                when (lotteryType) {
                    LotteryType.LtoBig -> {
                        LtoBigParser().also {
                            parserMap.put(lotteryType, it)
                        }
                    }
                    LotteryType.Lto -> {
                        LtoParser().also {
                            parserMap.put(lotteryType, it)
                        }
                    }
                    LotteryType.LtoHK -> {
                        LtoHKParser().also {
                            parserMap.put(lotteryType, it)
                        }
                    }
                    LotteryType.LtoList3 -> {
                        LtoList3Parser().also {
                            parserMap.put(lotteryType, it)
                        }
                    }
                    LotteryType.LtoList4 -> {
                        LtoList4Parser().also {
                            parserMap.put(lotteryType, it)
                        }
                    }
                    else -> throw IllegalArgumentException("Wrong lottery type")
                }
            }

        return Observable.create<Int> { emitter ->

            val allData = HashSet<LotteryEntity>()
            allData.addAll(getLotteryDataSingle(lotteryType).blockingGet())

            if (allData.isEmpty()) {
                val list = firestoreHelper.read(lotteryType).blockingGet()
                Timber.v("read from cloud, size: ${list.size}")
                if (list.isNotEmpty()) {
                    when (lotteryType) {
                        LotteryType.LtoBig -> {
                            lotteryDatabaseHelper.database.getLtoBigDao()
                                .insertAll(list.map { it as LtoBigEntity })
                        }
                        LotteryType.Lto -> {
                            lotteryDatabaseHelper.database.getLtoDao()
                                .insertAll(list.map { it as LtoEntity })
                        }
                        LotteryType.LtoHK -> {
                            lotteryDatabaseHelper.database.getLtoHKDao()
                                .insertAll(list.map { it as LtoHKEntity })
                        }
                        LotteryType.LtoList3 -> {
                            lotteryDatabaseHelper.database.getLtoList3Dao()
                                .insertAll(list.map { it as LtoList3Entity })
                        }
                        LotteryType.LtoList4 -> {
                            lotteryDatabaseHelper.database.getLtoList4Dao()
                                .insertAll(list.map { it as LtoList4Entity })
                        }
                        else -> throw IllegalArgumentException("Wrong lottery type")
                    }
                    allData.addAll(list)
                    lotteryDatabaseHelper.database.getResultDao()
                        .insert(LotteryResultEntity(true, lotteryType))
                }
            }

            val hasDoneResult =
                lotteryDatabaseHelper.database.getResultDao().query(lotteryType)
            val hasDone = if (hasDoneResult.isEmpty()) {
                false
            } else {
                hasDoneResult[0].done
            }

            fun complete(emitter: ObservableEmitter<Int>) {
                emitter.onComplete()
            }

            val url = parser.getUrl().blockingGet()
            Timber.v("url: $url")

            for (page in 1 until 500) {
                emitter.onNext(page)

                val data = parser.parseWithUrl(url, page)
                if (data.isEmpty()) {
                    complete(emitter)
                    break
                }

                val mapData = when (lotteryType) {
                    LotteryType.Lto -> {
                        data.map { item ->
                            val column1 = ArrayList<CellData>()
                            val column2 = ArrayList<CellData>()

                            for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                                column1.add(
                                    when {
                                        item.number.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isNormalNumber = true
                                            )
                                        }
                                        else -> {
                                            CellData(id = index, value = index)
                                        }
                                    }
                                )
                            }

                            for (index in Constants.LTO_COLUMN2_MIN..Constants.LTO_COLUMN2_MAX) {
                                column2.add(
                                    when {
                                        item.specialNumber.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isSpecialNumber = true
                                            )
                                        }
                                        else -> {
                                            CellData(id = index, value = index)
                                        }
                                    }
                                )
                            }

                            return@map LtoEntity(
                                item.date,
                                item.number,
                                item.specialNumber,
                                column1,
                                column2
                            )
                        }
                    }
                    LotteryType.LtoBig -> {
                        data.map { item ->
                            val column1 = ArrayList<CellData>()

                            for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                                column1.add(
                                    when {
                                        item.number.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isNormalNumber = true
                                            )
                                        }
                                        item.specialNumber.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isSpecialNumber = true
                                            )
                                        }
                                        else -> {
                                            CellData(id = index, value = index)
                                        }
                                    }
                                )
                            }
                            return@map LtoBigEntity(
                                item.date,
                                item.number,
                                item.specialNumber,
                                column1
                            )
                        }
                    }
                    LotteryType.LtoHK -> {
                        data.map { item ->
                            val column1 = ArrayList<CellData>()

                            for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                                column1.add(
                                    when {
                                        item.number.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isNormalNumber = true
                                            )
                                        }
                                        item.specialNumber.contains(index) -> {
                                            CellData(
                                                id = index,
                                                value = index,
                                                isSpecialNumber = true
                                            )
                                        }
                                        else -> {
                                            CellData(id = index, value = index)
                                        }
                                    }
                                )
                            }
                            return@map LtoHKEntity(
                                item.date,
                                item.number,
                                item.specialNumber,
                                column1
                            )
                        }
                    }
                    LotteryType.LtoList3 -> {
                        data.map { item ->
                            return@map LtoList3Entity(
                                item.date,
                                item.number
                            )
                        }
                    }
                    LotteryType.LtoList4 -> {
                        data.map { item ->
                            return@map LtoList4Entity(
                                item.date,
                                item.number
                            )
                        }
                    }
                    else -> throw IllegalArgumentException("Wrong lottery type")
                }

                val insertResult = when (lotteryType) {
                    LotteryType.Lto -> {
                        lotteryDatabaseHelper.database.getLtoDao()
                            .insertAll(mapData.map { it as LtoEntity })
                            .filter { it >= 0 }
                    }
                    LotteryType.LtoBig -> {
                        lotteryDatabaseHelper.database.getLtoBigDao()
                            .insertAll(mapData.map { it as LtoBigEntity })
                            .filter { it >= 0 }
                    }
                    LotteryType.LtoHK -> {
                        lotteryDatabaseHelper.database.getLtoHKDao()
                            .insertAll(mapData.map { it as LtoHKEntity })
                            .filter { it >= 0 }
                    }
                    LotteryType.LtoList3 -> {
                        lotteryDatabaseHelper.database.getLtoList3Dao()
                            .insertAll(mapData.map { it as LtoList3Entity })
                            .filter { it >= 0 }
                    }
                    LotteryType.LtoList4 -> {
                        lotteryDatabaseHelper.database.getLtoList4Dao()
                            .insertAll(mapData.map { it as LtoList4Entity })
                            .filter { it >= 0 }
                    }
                    else -> throw IllegalArgumentException("Wrong lottery type")
                }

                Timber.i("insertResult: ${insertResult.size}")

                if (insertResult.isEmpty()) {
                    if (hasDone) {
                        complete(emitter)
                        break
                    } else {
                        Timber.i(
                            "last time stamp: ${LotteryEntity.getTimeStamp(
                                lotteryType,
                                mapData[data.size - 1]
                            )}, page: $page"
                        )
                        val lastRecord = mapData.find {
                            LotteryEntity.getTimeStamp(
                                lotteryType,
                                it
                            ) == when (lotteryType) {
                                LotteryType.Lto -> 1201104000000
                                LotteryType.LtoHK -> 1025712000000
                                LotteryType.LtoBig -> 1073232000000
                                LotteryType.LtoList3 -> 1134921600000
                                LotteryType.LtoList4 -> 1049644800000
                                else -> IllegalArgumentException("Wrong lottery type")
                            }
                        }
                        if (lastRecord != null) {
                            lotteryDatabaseHelper.database.getResultDao()
                                .insert(LotteryResultEntity(true, lotteryType))
                            complete(emitter)
                            break
                        } else {
                            continue
                        }
                    }
                }

                allData.addAll(mapData)

                if (insertResult.isNotEmpty()) {
                    // update subtotal
                    fun update(previousCalendar: Calendar) {
                        val tempCalendar = Calendar.getInstance()
                        tempCalendar.timeInMillis = previousCalendar.timeInMillis
                        tempCalendar.set(
                            Calendar.DAY_OF_MONTH,
                            tempCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                        )
                        val min = tempCalendar.timeInMillis
                        tempCalendar.add(Calendar.MONTH, 1)
                        tempCalendar.set(
                            Calendar.DAY_OF_MONTH,
                            tempCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                        )
                        val max = tempCalendar.timeInMillis
                        val monthlyData = allData.filter {
                            LotteryEntity.getTimeStamp(
                                lotteryType,
                                it
                            ) in min until max
                        }
                        Timber.v(
                            "${previousCalendar.get(Calendar.YEAR)}/${previousCalendar.get(
                                Calendar.MONTH
                            ) + 1} > monthlyData size: ${monthlyData.size}"
                        )

                        when (lotteryType) {
                            LotteryType.Lto -> {
                                val column1 = ArrayList<CellData>()
                                val column2 = ArrayList<CellData>()

                                for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                                    column1.add(
                                        CellData(
                                            index, 0,
                                            isNormalNumber = false,
                                            isSpecialNumber = false
                                        )
                                    )
                                }
                                for (index in Constants.LTO_COLUMN2_MIN..Constants.LTO_COLUMN2_MAX) {
                                    column2.add(
                                        CellData(
                                            index, 0,
                                            isNormalNumber = false,
                                            isSpecialNumber = false
                                        )
                                    )
                                }
                                monthlyData.forEach { entity ->
                                    (entity as LtoEntity).rawNormalNumbers.forEach { number ->
                                        column1[number - 1].value = column1[number - 1].value + 1
                                    }
                                    entity.rawSpecialNumbers.forEach { number ->
                                        column2[number - 1].value = column2[number - 1].value + 1
                                    }
                                }
                                lotteryDatabaseHelper.database.getLtoDao().insertReplace(
                                    LtoEntity(
                                        max - 1,
                                        ArrayList(),
                                        ArrayList(),
                                        column1,
                                        column2,
                                        true
                                    ).also { newEntity ->
                                        allData.removeAll {
                                            LotteryEntity.getTimeStamp(
                                                lotteryType,
                                                it
                                            ) == newEntity.timeStamp
                                        }
                                        allData.add(newEntity)
                                        FirestoreService.write(
                                            context,
                                            lotteryType,
                                            ArrayList<LotteryEntity>().apply { add(newEntity) })
                                    }
                                )
                            }
                            LotteryType.LtoBig -> {
                                val column1 = ArrayList<CellData>()

                                for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                                    column1.add(
                                        CellData(
                                            index, 0,
                                            isNormalNumber = false,
                                            isSpecialNumber = false
                                        )
                                    )
                                }
                                monthlyData.forEach { entity ->
                                    (entity as LtoBigEntity).rawNormalNumbers.forEach { number ->
                                        column1[number - 1].value = column1[number - 1].value + 1
                                    }
                                    entity.rawSpecialNumbers.forEach { number ->
                                        column1[number - 1].value = column1[number - 1].value + 1
                                    }
                                }
                                lotteryDatabaseHelper.database.getLtoBigDao().insertReplace(
                                    LtoBigEntity(
                                        max - 1,
                                        ArrayList(),
                                        ArrayList(),
                                        column1,
                                        true
                                    ).also { newEntity ->
                                        allData.removeAll {
                                            LotteryEntity.getTimeStamp(
                                                lotteryType,
                                                it
                                            ) == newEntity.timeStamp
                                        }
                                        allData.add(newEntity)
                                        FirestoreService.write(
                                            context,
                                            lotteryType,
                                            ArrayList<LotteryEntity>().apply { add(newEntity) })
                                    }
                                )
                            }
                            LotteryType.LtoHK -> {
                                val column1 = ArrayList<CellData>()

                                for (index in Constants.LTO_BIG_MIN..Constants.LTO_BIG_MAX) {
                                    column1.add(
                                        CellData(
                                            index, 0,
                                            isNormalNumber = false,
                                            isSpecialNumber = false
                                        )
                                    )
                                }

                                monthlyData.forEach { entity ->
                                    (entity as LtoHKEntity).rawNormalNumbers.forEach { number ->
                                        column1[number - 1].value = column1[number - 1].value + 1
                                    }
                                    entity.rawSpecialNumbers.forEach { number ->
                                        column1[number - 1].value = column1[number - 1].value + 1
                                    }
                                }
                                lotteryDatabaseHelper.database.getLtoHKDao().insertReplace(
                                    LtoHKEntity(
                                        max - 1,
                                        ArrayList(),
                                        ArrayList(),
                                        column1,
                                        true
                                    ).also { newEntity ->
                                        allData.removeAll {
                                            LotteryEntity.getTimeStamp(
                                                lotteryType,
                                                it
                                            ) == newEntity.timeStamp
                                        }
                                        allData.add(newEntity)
                                        FirestoreService.write(
                                            context,
                                            lotteryType,
                                            ArrayList<LotteryEntity>().apply { add(newEntity) })
                                    }
                                )
                            }
                            LotteryType.LtoList3 -> {}
                            LotteryType.LtoList4 -> {}
                            else -> throw IllegalArgumentException("Unknown type")
                        }
                    }

                    var previousCalendar = Calendar.getInstance()
                    previousCalendar.timeInMillis = data[0].date

                    for (index in 1 until data.size) {
                        val currentCalendar = Calendar.getInstance()
                        currentCalendar.timeInMillis = data[index].date
                        if (previousCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)) {
                            // find monthly data in allData and calculate total count
                            update(previousCalendar)
                            previousCalendar = currentCalendar
                        }
                    }
                    previousCalendar.timeInMillis = data[data.size - 1].date
                    update(previousCalendar)

                    FirestoreService.write(context, lotteryType, mapData)
                }
            }
        }
    }
}