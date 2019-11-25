package bj4.dev.yhh.l.util

import android.content.Context

class SharedPreferenceHelper(context: Context) {

    companion object {
        const val PREFERENCE_GENERAL = "general"

        const val KEY_DISPLAY_TYPE = "KEY_DISPLAY_TYPE"

        const val DISPLAY_TYPE_ORDER = 0
        const val DISPLAY_TYPE_COMBINATION = 1
        const val DISPLAY_TYPE_END = 2
    }

    val generalSettings = context.getSharedPreferences(PREFERENCE_GENERAL, Context.MODE_PRIVATE)

    fun getDisplayType() = generalSettings.getInt(KEY_DISPLAY_TYPE, DISPLAY_TYPE_ORDER)

    fun setDisplayType(type: Int) = generalSettings.edit().putInt(KEY_DISPLAY_TYPE, type).apply()
}