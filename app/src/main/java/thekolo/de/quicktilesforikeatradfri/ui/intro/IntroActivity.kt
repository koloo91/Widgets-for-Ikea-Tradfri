package thekolo.de.quicktilesforikeatradfri.ui.intro

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import android.view.View
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil


class IntroActivity : MaterialIntroActivity() {

    private val gatewaySearchFragment = GatewaySearchFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title("Welcome to Quicktiles for Ikea TRÅDFRI")
                .description("Before you can use this app we have to setup some things.")
                .build())

        addSlide(gatewaySearchFragment, MessageButtonBehaviour(View.OnClickListener { searchForGateway() }, "Search"))
    }

    private fun searchForGateway() {
        gatewaySearchFragment.searchForGateway()
    }

    override fun onFinish() {
        SettingsUtil.setGatewayIp(applicationContext, gatewaySearchFragment.gatewayIp)

        super.onFinish()
    }
}