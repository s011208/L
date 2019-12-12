package bj4.dev.yhh.repository

import bj4.dev.yhh.repository.entity.*
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class FirestoreHelper {

    companion object {
        const val COLLECTION_LTO = "lto"
        const val COLLECTION_LTO_BIG = "lto_big"
        const val COLLECTION_LTO_HK = "lto_hk"
        const val COLLECTION_LTO_LIST3 = "lto_list3"
        const val COLLECTION_LTO_LIST4 = "lto_list4"
    }

    private val firestore = FirebaseFirestore.getInstance()

    private fun getCollectionKey(@LotteryType lotteryType: Int): String =
        when (lotteryType) {
            LotteryType.LtoBig -> COLLECTION_LTO
            LotteryType.Lto -> COLLECTION_LTO_BIG
            LotteryType.LtoHK -> COLLECTION_LTO_HK
            LotteryType.LtoList3 -> COLLECTION_LTO_LIST3
            LotteryType.LtoList4 -> COLLECTION_LTO_LIST4
            else -> throw IllegalArgumentException("Wrong lottery type")
        }

    fun read(@LotteryType lotteryType: Int): Single<List<LotteryEntity>> {
        return Single.create { emitter ->
            firestore.collection(getCollectionKey(lotteryType))
                .get()
                .addOnSuccessListener { snapshot ->
                    Timber.v("firebase read size: ${snapshot.documents.size}")
                    emitter.onSuccess(ArrayList<LotteryEntity>().apply {
                        addAll(
                            when (lotteryType) {
                                LotteryType.LtoBig -> snapshot.toObjects(LtoBigEntity::class.java)
                                LotteryType.Lto -> snapshot.toObjects(LtoEntity::class.java)
                                LotteryType.LtoHK -> snapshot.toObjects(LtoHKEntity::class.java)
                                LotteryType.LtoList3 -> snapshot.toObjects(LtoList3Entity::class.java)
                                LotteryType.LtoList4 -> snapshot.toObjects(LtoList4Entity::class.java)
                                else -> throw IllegalArgumentException("Wrong lottery type")
                            }
                        )
                    })
                }
                .addOnFailureListener {
                    Timber.w(it, "addOnFailureListener")
                    emitter.onError(it)
                }
        }
    }

    fun write(@LotteryType lotteryType: Int, dataList: ArrayList<LotteryEntity>): Completable {
        return Completable.create { emitter ->
            val map = HashMap<String, Any>()
            dataList.forEach {
                map[LotteryEntity.getTimeStamp(lotteryType, it).toString()] = it
            }

            val collection = firestore.collection(getCollectionKey(lotteryType))

            firestore.runBatch { writeBatch ->
                map.forEach { entry ->
                    writeBatch.set(collection.document(entry.key), entry.value)
                }
            }.addOnSuccessListener {
                Timber.v("addOnSuccessListener")
                emitter.onComplete()
            }.addOnFailureListener {
                Timber.w(it, "addOnFailureListener")
                emitter.onError(it)
            }
        }
    }
}