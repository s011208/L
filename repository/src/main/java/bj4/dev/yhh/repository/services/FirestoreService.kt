package bj4.dev.yhh.repository.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import bj4.dev.yhh.repository.FirestoreHelper
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.*
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class FirestoreService : IntentService("firestore service") {

    companion object {
        const val ACTION_WRITE_CLOUD = "action_write_cloud"

        const val KEY_LOTTERY_TYPE = "key_lottery_type"
        const val KEY_LOTTERY_DATA = "key_lottery_data"

        fun write(context: Context, lotteryType: Int, data: List<LotteryEntity>) {
            context.startService(Intent(context, FirestoreService::class.java).apply {
                action = ACTION_WRITE_CLOUD
                putExtra(KEY_LOTTERY_TYPE, lotteryType)
                putStringArrayListExtra(KEY_LOTTERY_DATA, ArrayList<String>().apply {
                    val gson = Gson()
                    data.forEach { add(gson.toJson(it)) }
                })
            })
        }
    }

    private val firestoreHelper: FirestoreHelper by inject()

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            ACTION_WRITE_CLOUD -> {
                val lotteryType = intent.getIntExtra(KEY_LOTTERY_TYPE, -1)
                if (lotteryType < 0) throw IllegalArgumentException("Wrong lottery type")
                val lotteryDataStringList = intent.getStringArrayListExtra(KEY_LOTTERY_DATA)
                    ?: throw IllegalArgumentException("Wrong lottery data")
                val lotteryDataList = ArrayList<LotteryEntity>().apply {
                    val gson = Gson()
                    lotteryDataStringList.forEach { string ->
                        val entity = when (lotteryType) {
                            LotteryType.LtoBig -> gson.fromJson(string, LtoBigEntity::class.java)
                            LotteryType.LtoHK -> gson.fromJson(string, LtoHKEntity::class.java)
                            LotteryType.Lto -> gson.fromJson(string, LtoEntity::class.java)
                            LotteryType.LtoList3 -> gson.fromJson(string, LtoList3Entity::class.java)
                            LotteryType.LtoList4 -> gson.fromJson(string, LtoList4Entity::class.java)
                            else -> throw IllegalArgumentException("Wrong lottery type")
                        }
                        add(entity)
                    }
                }
                firestoreHelper.write(lotteryType, lotteryDataList).blockingAwait()
            }
        }
    }
}