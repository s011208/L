package bj4.dev.yhh.tracker

import android.app.Activity
import android.content.Context
import android.os.Bundle

class TrackHelper(context: Context) {

    private val trackers = ArrayList<Tracker>()

    companion object {
        const val TRACK_NAME_PARSER_ERROR = "parser_error"
        const val TRACK_NAME_MENU = "menu"

        const val PARAM_ERROR = "error"
        const val PARAM_MENU_TYPE = "type"
        const val PARAM_SORTING_TYPE = "sorting_type"
    }

    init {
        trackers.add(FirebaseTracker(context))
    }

    fun trackScreen(activity: Activity, name: String) {
        trackers.forEach {
            it.trackScreen(activity, name)
        }
    }

    fun trackEvent(name: String, params: Bundle?) {
        trackers.forEach {
            it.trackEvent(name, params)
        }
    }
}