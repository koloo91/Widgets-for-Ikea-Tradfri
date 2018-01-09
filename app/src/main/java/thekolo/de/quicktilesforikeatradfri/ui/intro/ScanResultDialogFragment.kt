package thekolo.de.quicktilesforikeatradfri.ui.intro


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_scan_result_dialog.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.ui.adapter.GatewaySearchResultsAdapter
import java.util.*


class ScanResultDialogFragment : DialogFragment(), GatewaySearchResultsAdapter.ItemClickedListener {

    var listener: OnIpSelectedListener? = null

    private lateinit var ips: ArrayList<String>
    private lateinit var hostnames: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ips = arguments?.getStringArrayList(IPS_ARG) ?: ArrayList()
        hostnames = arguments?.getStringArrayList(HOSTNAMES_ARG) ?: ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_scan_result_dialog, container, false)

        val recyclerview = view.gateway_search_results_recycler_view
        recyclerview.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = GatewaySearchResultsAdapter(this, ips, hostnames)

        return view
    }

    override fun onItemClicked(ip: String) {
        listener?.onIpSelected(ip)
        dismiss()
    }

    companion object {
        const val IPS_ARG = "ips"
        const val HOSTNAMES_ARG = "hostnames"

        fun newInstance(devices: List<Pair<String, String>>): ScanResultDialogFragment {
            val fragment = ScanResultDialogFragment()

            val args = Bundle()
            args.putStringArrayList(IPS_ARG, devices.map { it.first } as ArrayList<String>)
            args.putStringArrayList(HOSTNAMES_ARG, devices.map { it.second } as ArrayList<String>)

            fragment.arguments = args

            return fragment
        }
    }

    interface OnIpSelectedListener {
        fun onIpSelected(ip: String)
    }
}
