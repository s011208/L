package bj4.dev.yhh.repository

import androidx.annotation.IntDef

@IntDef(LotteryType.LtoHK, LotteryType.LtoBig, LotteryType.Lto)
@Retention(AnnotationRetention.SOURCE)
annotation class LotteryType {
    companion object {
        const val LtoHK = 0
        const val LtoBig = 1
        const val Lto = 2
        const val LtoList3 = 3
        const val LtoList4 = 4
    }
}