package thekolo.de.widgetsforikeatradfri

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gateway_scan.*
import kotlinx.android.synthetic.main.fragment_gateway_scan.view.*
import kotlinx.coroutines.experimental.runBlocking
import thekolo.de.widgetsforikeatradfri.utils.NetworkUtils
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil


class GatewayScanFragment : Fragment() {
    var listener: OnGatewayScanFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_gateway_scan, container, false)

        view.search_button.setOnClickListener {
            startScan(view)
        }

        view.next_button.setOnClickListener {
            val ip = view.gateway_ip_edit_text.text.toString()
            SettingsUtil.setGatewayIp(context, ip)
            listener?.onGatewayScanFragmentNextButtonClicked()
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnGatewayScanFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnGatewayScanFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun startScan(view: View) {
        view.scan_progress_bar.visibility = View.VISIBLE
        view.scan_progress_bar.max = 100

        runBlocking {
            val ip = NetworkUtils.searchGatewayIp { progress ->
                view.scan_progress_bar.progress = progress
            }.await()

            if (ip == null)
                displayUnableToFindGatewayMessage()

            view.scan_progress_bar.visibility = View.INVISIBLE
            view.gateway_ip_edit_text.setText(ip ?: "")
        }
    }

    private fun displayUnableToFindGatewayMessage() {
        Snackbar.make(gateway_ip_edit_text, "Unable to find gateway in your network", Snackbar.LENGTH_LONG).setAction("OK", {

        }).show()
    }

    interface OnGatewayScanFragmentInteractionListener {
        fun onGatewayScanFragmentNextButtonClicked()
    }

    companion object {
        fun newInstance(): GatewayScanFragment {
            return GatewayScanFragment()
        }
    }
}
