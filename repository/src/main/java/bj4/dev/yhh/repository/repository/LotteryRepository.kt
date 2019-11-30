package bj4.dev.yhh.repository.repository

import android.util.SparseArray
import androidx.core.util.contains
import bj4.dev.yhh.lottery_parser.LotteryParser
import bj4.dev.yhh.lottery_parser.lto.LtoParser
import bj4.dev.yhh.lottery_parser.lto_big.LtoBigParser
import bj4.dev.yhh.lottery_parser.lto_hk.LtoHKParser
import bj4.dev.yhh.repository.CellData
import bj4.dev.yhh.repository.Constants
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.database.LotteryDatabaseHelper
import bj4.dev.yhh.repository.entity.LotteryResultEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import timber.log.Timber
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class LotteryRepository(private val lotteryDatabaseHelper: LotteryDatabaseHelper) {

    private val parserMap = SparseArray<LotteryParser>()

    fun getLto(): Observable<List<LtoEntity>> {
        val parser =
            if (parserMap.contains(LotteryType.Lto)) {
                parserMap.get(LotteryType.Lto)
            } else {
                LtoParser().also {
                    parserMap.put(LotteryType.Lto, it)
                }
            }

        return Observable.create<List<LtoEntity>> { emitter ->

            val hasDoneResult =
                lotteryDatabaseHelper.database.getResultDao().query(LotteryType.Lto)
            val hasDone = if (hasDoneResult.isEmpty()) {
                false
            } else {
                hasDoneResult[0].done
            }

            val allData = HashSet<LtoEntity>()

            fun complete(emitter: ObservableEmitter<List<LtoEntity>>) {
                emitter.onNext(lotteryDatabaseHelper.database.getLtoDao().query().blockingGet())
                emitter.onComplete()
            }

            for (page in 1 until Int.MAX_VALUE) {
                val data = parser.parseAsync(page).blockingGet()
                if (data.isEmpty()) {
                    complete(emitter)
                    break
                }

                val mapData = data.map { item ->
                    val column1 = ArrayList<CellData>()
                    val column2 = ArrayList<CellData>()

                    for (index in Constants.LTO_COLUMN1_MIN..Constants.LTO_COLUMN1_MAX) {
                        column1.add(
                            when {
                                item.number.contains(index) -> {
                                    CellData(id = index, value = index, isNormalNumber = true)
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
                                    CellData(id = index, value = index, isSpecialNumber = true)
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

                val insertResult =
                    lotteryDatabaseHelper.database.getLtoDao().insertAll(mapData)
                        .filter { it >= 0 }

                Timber.i("insertResult: ${insertResult.size}")

                if (insertResult.isEmpty()) {
                    if (hasDone) {
                        complete(emitter)
                        break
                    } else {
                        Timber.i("last time stamp of Lto: ${mapData[data.size - 1].timeStamp}, page: $page")
                        val lastRecord = mapData.find { it.timeStamp == 1201104000000 }
                        if (lastRecord != null) {
                            lotteryDatabaseHelper.database.getResultDao()
                                .insert(LotteryResultEntity(true, LotteryType.Lto))
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
                        val monthlyData = allData.filter { it.timeStamp in min until max }
                        Timber.v(
                            "${previousCalendar.get(Calendar.YEAR)}/${previousCalendar.get(
                                Calendar.MONTH
                            ) + 1} > monthlyData size: ${monthlyData.size}"
                        )

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
                            entity.rawNormalNumbers.forEach { number ->
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
                                allData.removeAll { it.timeStamp == newEntity.timeStamp }
                                allData.add(newEntity)
                            }
                        )
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
                }

                emitter.onNext(ArrayList(allData).also {
                    it.sortWith(Comparator { p0, p1 -> p0.timeStamp.compareTo(p1.timeStamp) })
                })
            }
        }
    }

    fun getLtoHK(): Observable<List<LtoHKEntity>> {
        val parser =
            if (parserMap.contains(LotteryType.LtoHK)) {
                parserMap.get(LotteryType.LtoHK)
            } else {
                LtoHKParser().also {
                    parserMap.put(LotteryType.LtoHK, it)
                }
            }

        return Observable.create<List<LtoHKEntity>> { emitter ->

            val hasDoneResult =
                lotteryDatabaseHelper.database.getResultDao().query(LotteryType.LtoHK)
            val hasDone = if (hasDoneResult.isEmpty()) {
                false
            } else {
                hasDoneResult[0].done
            }

            val allData = HashSet<LtoHKEntity>()

            fun complete(emitter: ObservableEmitter<List<LtoHKEntity>>) {
                emitter.onNext(lotteryDatabaseHelper.database.getLtoHKDao().query().blockingGet())
                emitter.onComplete()
            }

            for (page in 1 until Int.MAX_VALUE) {
                val data = parser.parseAsync(page).blockingGet()
                if (data.isEmpty()) {
                    complete(emitter)
                    break
                }

                Timber.v("data size: ${data.size}")

                val mapData = data.map { item ->
                    val column1 = ArrayList<CellData>()

                    for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                        column1.add(
                            when {
                                item.number.contains(index) -> {
                                    CellData(id = index, value = index, isNormalNumber = true)
                                }
                                item.specialNumber.contains(index) -> {
                                    CellData(id = index, value = index, isSpecialNumber = true)
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

                val insertResult =
                    lotteryDatabaseHelper.database.getLtoHKDao().insertAll(mapData)
                        .filter { it >= 0 }

                Timber.i("insertResult: ${insertResult.size}")

                if (insertResult.isEmpty()) {
                    if (hasDone) {
                        complete(emitter)
                        break
                    } else {
                        Timber.i("last time stamp of LtoHK: ${mapData[data.size - 1].timeStamp}, page: $page")
                        val lastRecord = mapData.find { it.timeStamp == 1025712000000 }
                        if (lastRecord != null) {
                            lotteryDatabaseHelper.database.getResultDao()
                                .insert(LotteryResultEntity(true, LotteryType.LtoHK))
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
                        val monthlyData = allData.filter { it.timeStamp in min until max }
                        Timber.v(
                            "${previousCalendar.get(Calendar.YEAR)}/${previousCalendar.get(
                                Calendar.MONTH
                            ) + 1} > monthlyData size: ${monthlyData.size}"
                        )

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
                            entity.rawNormalNumbers.forEach { number ->
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
                                allData.removeAll { it.timeStamp == newEntity.timeStamp }
                                allData.add(newEntity)
                            }
                        )
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
                }

                emitter.onNext(ArrayList(allData).also {
                    it.sortWith(Comparator { p0, p1 -> p0.timeStamp.compareTo(p1.timeStamp) })
                })
            }
        }
    }

    fun getLtoBig(): Observable<List<LtoBigEntity>> {
        val parser =
            if (parserMap.contains(LotteryType.LtoBig)) {
                parserMap.get(LotteryType.LtoBig)
            } else {
                LtoBigParser().also {
                    parserMap.put(LotteryType.LtoBig, it)
                }
            }

        return Observable.create<List<LtoBigEntity>> { emitter ->

            val hasDoneResult =
                lotteryDatabaseHelper.database.getResultDao().query(LotteryType.LtoBig)
            val hasDone = if (hasDoneResult.isEmpty()) {
                false
            } else {
                hasDoneResult[0].done
            }

            val allData = HashSet<LtoBigEntity>()

            fun complete(emitter: ObservableEmitter<List<LtoBigEntity>>) {
                emitter.onNext(lotteryDatabaseHelper.database.getLtoBigDao().query().blockingGet())
                emitter.onComplete()
            }

            for (page in 1 until Int.MAX_VALUE) {
                val data = parser.parseAsync(page).blockingGet()
                if (data.isEmpty()) {
                    complete(emitter)
                    break
                }

                val mapData = data.map { item ->
                    val column1 = ArrayList<CellData>()

                    for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                        column1.add(
                            when {
                                item.number.contains(index) -> {
                                    CellData(id = index, value = index, isNormalNumber = true)
                                }
                                item.specialNumber.contains(index) -> {
                                    CellData(id = index, value = index, isSpecialNumber = true)
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

                val insertResult =
                    lotteryDatabaseHelper.database.getLtoBigDao().insertAll(mapData)
                        .filter { it >= 0 }

                Timber.i("insertResult: ${insertResult.size}")

                if (insertResult.isEmpty()) {
                    if (hasDone) {
                        complete(emitter)
                        break
                    } else {
                        Timber.i("last time stamp of LtoBig: ${mapData[data.size - 1].timeStamp}, page: $page")
                        val lastRecord = mapData.find { it.timeStamp == 1073232000000 }
                        if (lastRecord != null) {
                            lotteryDatabaseHelper.database.getResultDao()
                                .insert(LotteryResultEntity(true, LotteryType.LtoBig))
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
                        val monthlyData = allData.filter { it.timeStamp in min until max }
                        Timber.v(
                            "${previousCalendar.get(Calendar.YEAR)}/${previousCalendar.get(
                                Calendar.MONTH
                            ) + 1} > monthlyData size: ${monthlyData.size}"
                        )

                        val column1 = ArrayList<CellData>()

                        for (index in Constants.LTO_HK_MIN..Constants.LTO_HK_MAX) {
                            column1.add(
                                CellData(
                                    index, 0,
                                    isNormalNumber = false,
                                    isSpecialNumber = false
                                )
                            )
                        }
                        monthlyData.forEach { entity ->
                            entity.rawNormalNumbers.forEach { number ->
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
                                allData.removeAll { it.timeStamp == newEntity.timeStamp }
                                allData.add(newEntity)
                            }
                        )
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
                }

                emitter.onNext(ArrayList(allData).also {
                    it.sortWith(Comparator { p0, p1 -> p0.timeStamp.compareTo(p1.timeStamp) })
                })
            }
        }
    }
}