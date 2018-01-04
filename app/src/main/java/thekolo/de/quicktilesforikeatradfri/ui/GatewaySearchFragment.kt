package thekolo.de.quicktilesforikeatradfri.ui


import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import thekolo.de.quicktilesforikeatradfri.R


class GatewaySearchFragment : SlideFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gateway_search, container, false)
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun backgroundColor(): Int {
        return R.color.colorPrimary
    }
}
