package bj4.dev.yhh.tracker

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebaseTracker(context: Context) : Tracker() {
    private val tracker = FirebaseAnalytics.getInstance(context)

    override fun trackScreen(activity: Activity, name: String) =
        tracker.setCurrentScreen(activity, name, null)

    override fun trackEvent(name: String, params: Bundle?) {
        tracker.logEvent(name, params)
    }
}