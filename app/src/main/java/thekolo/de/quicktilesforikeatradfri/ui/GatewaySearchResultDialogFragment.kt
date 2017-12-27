package thekolo.de.quicktilesforikeatradfri.ui


import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_gateway_search_result.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.ui.adapter.GatewaySearchResultsAdapter
import thekolo.de.quicktilesforikeatradfri.utils.NetworkUtils


class GatewaySearchResultDialogFragment : DialogFragment() {

    private val adapter = GatewaySearchResultsAdapter(mutableListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gateway_search_result, container, false)

        val recyclerview = view.gateway_search_results_recycler_view
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(activity.applicationContext)
        recyclerview.adapter = adapter

        NetworkUtils.searchGatewayIp({ gatewayIp ->
            println("Found IP: $gatewayIp")
        }, {
            println("Error")
        }, { newDevice ->
            println("Found new device: $newDevice")
            adapter.addIp(newDevice)
        }, { progress ->

        })

        return view
    }


    companion object {
        fun newInstance(): GatewaySearchResultDialogFragment {
            return GatewaySearchResultDialogFragment()
        }
    }
}
