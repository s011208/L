package bj4.dev.yhh.tracker

import android.app.Activity
import android.os.Bundle

internal abstract class Tracker {
    abstract fun trackScreen(activity: Activity, name: String)
    abstract fun trackEvent(name: String, params: Bundle? = null)
}