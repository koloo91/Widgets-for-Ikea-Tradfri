package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.gateway_search_results_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.R

class GatewaySearchResultsAdapter(private val ips: MutableList<String>) : RecyclerView.Adapter<GatewaySearchResultsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return

        val ip = ips[position]
        holder.ipTextView.text = ip
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.gateway_search_results_recycler_view_item, parent, false)

        return GatewaySearchResultsAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ips.size
    }

    fun addIp(ip: String) {
        ips.add(ip)
        ips.sort()

        val index = ips.indexOf(ip)
        this.notifyItemInserted(index)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ipTextView = view.gateway_ip_text_view
    }
}