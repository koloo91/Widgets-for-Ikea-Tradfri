package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil


class OnboardingActivity : AppCompatActivity(), WelcomeFragment.OnWelcomeFragmentInteractionListener, GatewayScanFragment.OnGatewayScanFragmentInteractionListener, SecurityIdFragment.OnSecurityIdFragmentInteractionListener {
    private val fragmentHolder: MutableMap<String, Fragment> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        displayWelcomeFragment()
    }

    private fun displayWelcomeFragment() {
        var fragment = getFragment("welcome_fragment") as WelcomeFragment?
        if (fragment == null) {
            fragment = WelcomeFragment.newInstance()

            fragment.listener = this
            setFragment("welcome_fragment", fragment)
        }
        displayFragment(fragment)
    }

    private fun displayGatewayScanFragment() {
        var fragment = getFragment("gateway_scan_fragment") as GatewayScanFragment?
        if (fragment == null) {
            fragment = GatewayScanFragment.newInstance()

            fragment.listener = this
            setFragment("gateway_scan_fragment", fragment)
        }
        displayFragment(fragment)
    }

    private fun displaySecurityIdFragment() {
        var fragment = getFragment("security_id_fragment") as SecurityIdFragment?
        if (fragment == null) {
            fragment = SecurityIdFragment.newInstance()

            fragment.listener = this
            setFragment("security_id_fragment", fragment)
        }
        displayFragment(fragment)
    }

    private fun displayFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
    }

    private fun getFragment(fragmentId: String): Fragment? {
        return fragmentHolder[fragmentId]
    }

    private fun setFragment(fragmentId: String, fragment: Fragment) {
        fragmentHolder[fragmentId] = fragment
    }

    override fun onWelcomeFragmentNextButtonClicked() {
        displayGatewayScanFragment()
    }

    override fun onGatewayScanFragmentNextButtonClicked() {
        displaySecurityIdFragment()
    }

    override fun onSecurityIdFragmentFinishClicked() {
        //SettingsUtil.setOnboardingCompleted(applicationContext, true)
        finish()
    }
}