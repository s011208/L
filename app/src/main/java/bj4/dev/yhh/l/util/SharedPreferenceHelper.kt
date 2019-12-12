package bj4.dev.yhh.l.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.TypedValue
import androidx.preference.PreferenceManager
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.settings.fragment.main.MainSettingsFragment


class SharedPreferenceHelper(private val context: Context) {

    companion object {
        const val PREFERENCE_GENERAL = "general"

        const val KEY_DISPLAY_TYPE = "KEY_DISPLAY_TYPE"

        const val DISPLAY_TYPE_ORDER = 0
        const val DISPLAY_TYPE_COMBINATION = 1
        const val DISPLAY_TYPE_END = 2

        private fun getPixels(unit: Int, size: Float): Int =
            TypedValue.applyDimension(unit, size, Resources.getSystem().displayMetrics).toInt()
    }

    val generalSettings: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_GENERAL, Context.MODE_PRIVATE)

    private val preferenceManager: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getSortingType() = generalSettings.getInt(KEY_DISPLAY_TYPE, DISPLAY_TYPE_ORDER)

    fun setDisplayType(type: Int) = generalSettings.edit().putInt(KEY_DISPLAY_TYPE, type).apply()

    fun getLargeTableTextSize(): Float =
        context.resources.getIntArray(R.array.preference_large_table_text_size)[preferenceManager.getString(
            MainSettingsFragment.KEY_LARGE_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density

    fun getLargeTableCellWidth(): Int =
        (context.resources.getIntArray(R.array.preference_large_table_cell_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_LARGE_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()

    fun getLargeTableCellDateWidth(): Int =
        (context.resources.getIntArray(R.array.preference_large_table_cell_date_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_LARGE_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()

    fun getSmallTableTextSize(): Float =
        context.resources.getIntArray(R.array.preference_small_table_text_size)[preferenceManager.getString(
            MainSettingsFragment.KEY_SMALL_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density

    fun getSmallTableCellWidth(): Int =
        (context.resources.getIntArray(R.array.preference_small_table_cell_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_SMALL_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()

    fun getSmallTableCellDateWidth(): Int =
        (context.resources.getIntArray(R.array.preference_small_table_cell_date_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_SMALL_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()

    fun getListTableTextSize(): Float =
        context.resources.getIntArray(R.array.preference_list_table_text_size)[preferenceManager.getString(
            MainSettingsFragment.KEY_LIST_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density

    fun getListTableCellWidth(): Int =
        (context.resources.getIntArray(R.array.preference_list_table_cell_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_LIST_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()

    fun getListTableCellDateWidth(): Int =
        (context.resources.getIntArray(R.array.preference_list_table_cell_date_width)[preferenceManager.getString(
            MainSettingsFragment.KEY_LIST_TABLE_TEXT_SIZE,
            "2"
        )!!.toInt()] * Resources.getSystem().displayMetrics.density).toInt()
}