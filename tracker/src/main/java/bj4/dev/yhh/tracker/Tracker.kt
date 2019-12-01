package bj4.dev.yhh.tracker

import android.app.Activity

internal abstract class Tracker {
    abstract fun trackScreen(activity: Activity, name: String)
}