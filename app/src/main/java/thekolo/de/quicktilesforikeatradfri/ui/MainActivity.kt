package thekolo.de.quicktilesforikeatradfri.ui

import android.app.Fragment
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.action_bar_switch.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Job
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.ui.intro.IntroActivity
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil
import thekolo.de.quicktilesforikeatradfri.widgets.UpdateJobService
import thekolo.de.widgetsforikeatradfri.DonationsActivity
import java.util.*


class MainActivity : AppCompatActivity() {

    /*private val ip = "192.168.178.56"
    private val securityId = "vBPnZjwbl07N8rex"*/

    private val service: TradfriService
        get() = TradfriService.instance(applicationContext)

    private val fragments: MutableMap<String, Fragment> = mutableMapOf()

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        bottom_navigation.selectedItemId = R.id.devices

        UpdateJobService.schedule(applicationContext)

        if (displayIntroActivity()) {
            val introIntent = Intent(this, IntroActivity::class.java)
            startActivity(introIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        val item = menu?.findItem(R.id.action_toggle_all)
        item?.setActionView(R.layout.action_bar_switch)

        val switch = item?.actionView?.toggle_all_switch
        switch?.setOnCheckedChangeListener(this::toggleAll)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return true

        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.action_donate -> {
                val donationsActivity = Intent(this, DonationsActivity::class.java)
                startActivity(donationsActivity)
            }
        }

        return true
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.devices -> {
                println("DevicesSelected")
                displayDevicesFragment()
            }
            R.id.groups -> {
                println("GroupsSelected")
                displayGroupsFragment()
            }
            R.id.tiles -> {
                println("TilesSelected")
                displayTilesFragment()
            }
            else -> displayDevicesFragment()
        }

        return@OnNavigationItemSelectedListener true
    }

    fun startLoadingProcess(loadingFunction: () -> Unit, retryCounter: Int = 3): Job? {
        if (displayIntroActivity()) return null

        if (appHasBeenConfigured() && service.isRegistered(applicationContext)) {
            return service.ping({ _ ->
                configuration_hint_text_view.visibility = View.GONE
                loadingFunction()
            }, {
                if (retryCounter > 0) {
                    Log.d("MainActivity", "Ping failed.. retrying $retryCounter")
                    startLoadingProcess(loadingFunction, retryCounter - 1)
                } else displayMessage(getString(R.string.unable_to_reach_gateway))
            })
        } else if (appHasBeenConfigured()) {
            return startRegisterProcess {
                configuration_hint_text_view.visibility = View.GONE
                service.refreshClient(applicationContext)
                loadingFunction()
            }
        } else {
            configuration_hint_text_view.visibility = View.VISIBLE
        }

        return null
    }

    private fun displayIntroActivity(): Boolean {
        return !SettingsUtil.getOnboardingCompleted(applicationContext)
                || (SettingsUtil.getGatewayIp(applicationContext) ?: "").isEmpty()
                || (SettingsUtil.getSecurityId(applicationContext) ?: "").isEmpty()
    }

    private fun appHasBeenConfigured(): Boolean {
        val gatewayIp = SettingsUtil.getGatewayIp(this)
        val securityId = SettingsUtil.getSecurityId(this)

        return gatewayIp != null && gatewayIp.isNotEmpty() && securityId != null && securityId.isNotEmpty()
    }

    private fun startRegisterProcess(onFinish: () -> Unit): Job? {
        if (service.isRegistered(applicationContext)) return null

        service.refreshClient(applicationContext)

        val identity = "${UUID.randomUUID()}"
        return service.register(identity, { registerResult ->
            SettingsUtil.setIdentity(applicationContext, identity)
            SettingsUtil.setPreSharedKey(applicationContext, registerResult.preSharedKey)

            onFinish()
        }, { onError("Unable to register app at gateway! Please try it later again") })
    }

    fun displayMessage(message: String) {
        Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG).setAction("Ok", { _ -> }).show()
    }

    fun onError() {
        onError("An unexpected error occurred!")
    }

    private fun onError(message: String) {
        displayMessage(message)
    }

    private fun toggleAll(view: View, checked: Boolean) {
        if (checked) service.toggleAllOn(this::onToggleAllFinished)
        else service.toggleAllOff(this::onToggleAllFinished)
    }

    private fun onToggleAllFinished() {
        if (currentFragment == null) return
        refreshCurrentFragment(currentFragment!!)
    }

    private fun refreshCurrentFragment(fragment: Fragment) {
        when (fragment) {
            is DevicesFragment -> fragment.loadDevices()
            is GroupsFragment -> fragment.loadGroups()
        }
    }

    private fun displayDevicesFragment() {
        var fragment = fragments["DevicesFragment"]
        if (fragment == null) {
            fragment = DevicesFragment.newInstance()
            fragments["DevicesFragment"] = fragment
        }

        displayFragment(fragment)
    }

    private fun displayGroupsFragment() {
        var fragment = fragments["GroupsFragment"]
        if (fragment == null) {
            fragment = GroupsFragment.newInstance()
            fragments["GroupsFragment"] = fragment
        }

        displayFragment(fragment)
    }

    private fun displayTilesFragment() {
        var fragment = fragments["TilesFragment"]
        if (fragment == null) {
            fragment = TilesFragment.newInstance()
            fragments["TilesFragment"] = fragment
        }

        displayFragment(fragment)
    }



    private fun displayFragment(fragment: Fragment) {
        currentFragment = fragment

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }
}

