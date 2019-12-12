package bj4.dev.yhh.l.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.ui.activity.main.dialog.SortingDialogFragment
import bj4.dev.yhh.l.ui.activity.settings.SettingsActivity
import bj4.dev.yhh.tracker.TrackHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_SETTINGS = 10001
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val activityViewModel: MainActivityViewModel by viewModel()

    private val trackHelper: TrackHelper by inject()

    private var currentFragmentId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_lto_hk,
                R.id.nav_lto_big,
                R.id.nav_lto,
                R.id.nav_small_lto_hk,
                R.id.nav_small_lto_big,
                R.id.nav_small_lto,
                R.id.nav_lto_list3,
                R.id.nav_lto_list4
            ), drawerLayout
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentFragmentId = destination.id
            invalidateOptionsMenu()
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        activityViewModel.sortingType.observe(this, Observer { sortingType ->
            val fragment = getCurrentFragment()
            if (fragment is MainActivityActions) {
                fragment.onSortingTypeChanged(sortingType)
            }

            trackHelper.trackEvent(TrackHelper.TRACK_NAME_MENU, Bundle().apply {
                putString(TrackHelper.PARAM_MENU_TYPE, "sorting")
                putInt(TrackHelper.PARAM_SORTING_TYPE, sortingType)
            })
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        when (currentFragmentId) {
            R.id.nav_lto, R.id.nav_lto_big, R.id.nav_lto_hk -> {
                menu.children.forEach { if (it.itemId == R.id.action_sorting) it.isEnabled = true }
            }
            R.id.nav_small_lto, R.id.nav_small_lto_big, R.id.nav_small_lto_hk,
            R.id.nav_lto_list3, R.id.nav_lto_list4 -> {
                menu.children.forEach { if (it.itemId == R.id.action_sorting) it.isEnabled = false }
            }
            else -> {
                throw IllegalArgumentException("unexpected fragment id")
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.v("requestCode: $requestCode, resultCode: $resultCode")
        when (requestCode) {
            REQUEST_CODE_SETTINGS -> {
                getCurrentFragment()?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivityForResult(
                    Intent(this, SettingsActivity::class.java),
                    REQUEST_CODE_SETTINGS
                )
                trackHelper.trackEvent(TrackHelper.TRACK_NAME_MENU, Bundle().apply {
                    putString(TrackHelper.PARAM_MENU_TYPE, "settings")
                })
                true
            }
            R.id.action_sorting -> {
                SortingDialogFragment().show(supportFragmentManager, "SortingDialogFragment")
                true
            }
            R.id.action_move_to_top -> {
                val fragment = getCurrentFragment()
                if (fragment is MainActivityActions) {
                    fragment.onMoveToTop()
                }
                trackHelper.trackEvent(TrackHelper.TRACK_NAME_MENU, Bundle().apply {
                    putString(TrackHelper.PARAM_MENU_TYPE, "move to top")
                })
                true
            }
            R.id.action_move_to_bottom -> {
                val fragment = getCurrentFragment()
                if (fragment is MainActivityActions) {
                    fragment.onMoveToBottom()
                }
                trackHelper.trackEvent(TrackHelper.TRACK_NAME_MENU, Bundle().apply {
                    putString(TrackHelper.PARAM_MENU_TYPE, "move to bottom")
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getCurrentFragment(): androidx.fragment.app.Fragment? {
        return supportFragmentManager.fragments[0].childFragmentManager.fragments[0]
    }

    override fun onResume() {
        super.onResume()
        activityViewModel.resume()
    }

    override fun onPause() {
        super.onPause()
        activityViewModel.pause()
    }
}
