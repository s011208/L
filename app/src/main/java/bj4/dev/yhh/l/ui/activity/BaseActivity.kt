package bj4.dev.yhh.l.ui.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bj4.dev.yhh.l.ui.activity.main.MainActivity
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    private val sharedPreferenceHelper: SharedPreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateOrientation()
    }

    private fun updateOrientation() {
        requestedOrientation = if (sharedPreferenceHelper.getIsLandscapeMode()) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            MainActivity.REQUEST_CODE_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    updateOrientation()
                }
            }
        }
    }
}