package bj4.dev.yhh.tracker

import android.app.Activity
import android.content.Context

class TrackHelper(context: Context) {

    private val trackers = ArrayList<Tracker>()

    init {
        trackers.add(FirebaseTracker(context))
    }

    fun trackScreen(activity: Activity, name: String) {
        trackers.forEach {
            it.trackScreen(activity, name)
        }
    }
}