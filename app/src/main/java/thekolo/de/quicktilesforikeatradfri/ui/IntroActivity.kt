package thekolo.de.quicktilesforikeatradfri.ui

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import android.view.View
import thekolo.de.quicktilesforikeatradfri.R


class IntroActivity: MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .title("Welcome to Quicktiles for Ikea TRÅDFRI")
                .description("Before you can use this app we have to setup some things.")
                .build())

        addSlide(GatewaySearchFragment(), MessageButtonBehaviour(View.OnClickListener { displayGatewaySearchResultFragmentDialog() }, "Search"))

    }

    private fun displayGatewaySearchResultFragmentDialog() {
        val transaction = fragmentManager.beginTransaction()
        val prev = fragmentManager.findFragmentByTag("gateway_search_result_dialog")

        if(prev != null)
            transaction.remove(prev)

        val dialog = GatewaySearchResultDialogFragment.newInstance()
        dialog.show(transaction, "gateway_search_result_dialog")
    }
}