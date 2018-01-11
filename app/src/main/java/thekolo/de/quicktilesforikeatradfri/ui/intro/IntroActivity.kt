package thekolo.de.quicktilesforikeatradfri.ui.intro

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.content.Context
import android.os.Bundle
import android.view.View
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil


class IntroActivity : MaterialIntroActivity() {

    private val gatewaySearchFragment = GatewaySearchFragment()
    private val securityCodeFragment = SecurityCodeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title("Welcome to Quicktiles for Ikea TRÅDFRI")
                .description("Before you can use this app we have to setup some things.")
                .build())

        addSlide(gatewaySearchFragment, MessageButtonBehaviour(View.OnClickListener { searchForGateway() }, "Search"))
        addSlide(securityCodeFragment, MessageButtonBehaviour(View.OnClickListener { scanSecurityCode() }, "Scan"))
    }

    private fun searchForGateway() {
        gatewaySearchFragment.searchForGateway()
    }

    private fun scanSecurityCode() {
        securityCodeFragment.scanSecurityCode()
    }

    override fun onFinish() {
        SettingsUtil.setGatewayIp(applicationContext, gatewaySearchFragment.gatewayIp)
        SettingsUtil.setSecurityId(applicationContext, securityCodeFragment.securityCode)
        SettingsUtil.setOnboardingCompleted(applicationContext, true)

        super.onFinish()
    }
}