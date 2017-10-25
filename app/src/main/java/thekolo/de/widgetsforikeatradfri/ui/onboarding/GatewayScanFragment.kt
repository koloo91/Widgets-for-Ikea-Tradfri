package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_gateway_scan.*
import kotlinx.android.synthetic.main.fragment_gateway_scan.view.*
import kotlinx.coroutines.experimental.launch
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.coroutines.Android
import thekolo.de.widgetsforikeatradfri.utils.NetworkUtils
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil


class GatewayScanFragment : Fragment(), TextWatcher {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_gateway_scan, container, false)

        view.search_button.setOnClickListener {
            startScan(view)
        }

        view.gateway_ip_edit_text.addTextChangedListener(this)
        return view
    }

    private fun startScan(view: View) {
        view.scan_progress_bar.visibility = View.VISIBLE
        view.scan_progress_bar.max = 100

        launch(Android) {
            val asyncIp = NetworkUtils.searchGatewayIp { progress ->
                view.scan_progress_bar.progress = progress
            }

            val ip = asyncIp.await()
            view.scan_progress_bar.visibility = View.INVISIBLE
            view.gateway_ip_edit_text.setText(ip ?: "")

            if(ip == null)
                displayErrorMessage()
        }
    }

    private fun displayErrorMessage() {
        Snackbar.make(view!!, "Unable to find gateway ip. Please enter it manually.", Snackbar.LENGTH_LONG).setAction("OK", {

        }).show()
    }

    override fun afterTextChanged(s: Editable?) {
        val ip = s?.toString() ?: ""
        SettingsUtil.setGatewayIp(activity, ip)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    companion object {
        fun newInstance(): GatewayScanFragment {
            return GatewayScanFragment()
        }
    }
}
