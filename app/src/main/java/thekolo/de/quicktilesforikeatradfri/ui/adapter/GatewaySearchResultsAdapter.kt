package thekolo.de.quicktilesforikeatradfri.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.gateway_search_results_recycler_view_item.view.*
import thekolo.de.quicktilesforikeatradfri.R

class GatewaySearchResultsAdapter(private val listener: ItemClickedListener, private val ips: List<String>, private val hostnames: List<String>) : RecyclerView.Adapter<GatewaySearchResultsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder == null) return

        val hostname = hostnames[position]
        val ip = ips[position]
        holder.hostnameTextView.text = hostname
        holder.ipTextView.text = ip

        holder.rootView.setOnClickListener {
            println("OnClick")
            listener.onItemClicked(ip)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.gateway_search_results_recycler_view_item, parent, false)

        return GatewaySearchResultsAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ips.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rootView: View = view.root_view
        val hostnameTextView: TextView = view.hostname_text_view
        val ipTextView: TextView = view.ip_text_view
    }

    interface ItemClickedListener {
        fun onItemClicked(ip: String)
    }
}