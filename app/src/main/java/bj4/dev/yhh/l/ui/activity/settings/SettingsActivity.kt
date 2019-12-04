package bj4.dev.yhh.l.ui.activity.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.settings.fragment.main.MainSettingsFragment

class SettingsActivity : AppCompatActivity() {

    var fragmentResult: Int = Activity.RESULT_CANCELED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainSettingsFragment())
                .commit()
        }
    }

    override fun finish() {
        setResult(fragmentResult, Intent())
        super.finish()
    }
}