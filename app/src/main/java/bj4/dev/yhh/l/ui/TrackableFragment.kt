package bj4.dev.yhh.l.ui

import androidx.fragment.app.Fragment
import bj4.dev.yhh.tracker.TrackHelper
import org.koin.android.ext.android.inject

open class TrackableFragment : Fragment() {

    private val trackHelper: TrackHelper by inject()

    override fun onResume() {
        super.onResume()
        trackHelper.trackScreen(activity!!, this.javaClass.simpleName)
    }
}