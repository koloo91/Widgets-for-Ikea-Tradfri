package thekolo.de.quicktilesforikeatradfri.ui.intro


import agency.tango.materialintroscreen.SlideFragment
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fragment_gateway_search.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.NetworkUtils
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil
import thekolo.de.quicktilesforikeatradfri.utils.ValidateUtil


class GatewaySearchFragment : SlideFragment(), TextWatcher, ScanResultDialogFragment.OnIpSelectedListener {

    var gatewayIp = ""
        set(value) {
            Log.i("GatewaySearchFragment", "Setting gateway ip $value")
            SettingsUtil.setGatewayIp(activity!!.applicationContext, value)
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gateway_search, container, false)
        view.gateway_ip_edit_text.addTextChangedListener(this)
        return view
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun backgroundColor(): Int {
        return R.color.colorPrimary
    }

    override fun canMoveFurther(): Boolean {
        return ValidateUtil.isValidIp(gatewayIp) && !gatewayIp.isEmpty()
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return "Please enter your gateways Ip address!"
    }

    fun searchForGateway() {
        view!!.search_progress_bar.visibility = View.VISIBLE
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        val foundDevices = mutableListOf<Pair<String, String>>()

        NetworkUtils.searchGatewayIp({ gatewayIp ->
            onIpFound(gatewayIp)
        }, {
            println("Error")
        }, { deviceData ->
            println("Found new device: ${deviceData.first} - ${deviceData.second}")
            foundDevices.add(deviceData)
        }, { progress ->
            view!!.search_progress_bar.progress = progress
        }, {
            onSearchCompleted()
            if (gatewayIp.isEmpty())
                showDialog(foundDevices)
        })
    }

    private fun onSearchCompleted() {
        println("onSearchCompleted")
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        view?.search_progress_bar?.visibility = View.GONE
    }

    private fun onIpFound(ip: String) {
        println("onIpFound $ip")
        view?.gateway_ip_edit_text?.setText(ip)
        gatewayIp = ip
    }

    private fun showDialog(devices: List<Pair<String, String>>) {

        val alertDialogBuilder = AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)

        if (devices.isNotEmpty()) {
            alertDialogBuilder.setTitle("Select gateway")
            alertDialogBuilder.setMessage("Unable to find gateway. Please select one from the list which will be displayed after this message.")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                val fragmentTransaction = fragmentManager!!.beginTransaction()
                val previousFragment = fragmentManager!!.findFragmentByTag("dialog")
                if (previousFragment != null) {
                    fragmentTransaction.remove(previousFragment)
                }
                fragmentTransaction.addToBackStack(null)

                // Create and show the dialog.
                val newFragment = ScanResultDialogFragment.newInstance(devices)
                newFragment.listener = this@GatewaySearchFragment
                newFragment.show(fragmentTransaction, "dialog")
            }
            alertDialogBuilder.setNegativeButton("Cancel", { _, _ -> })
        } else {
            alertDialogBuilder.setTitle("Enter gateway ip")
            alertDialogBuilder.setMessage("Unable to find gateway.")
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setPositiveButton("OK", { _, _ -> })
        }

        alertDialogBuilder.create().show()
    }

    override fun onIpSelected(ip: String) {
        println("onIpSelected $ip")
        view?.gateway_ip_edit_text?.setText(ip)
        gatewayIp = ip
    }

    override fun afterTextChanged(s: Editable?) {
        if (s == null) return
        if (!ValidateUtil.isValidIp(s.toString())) {
            view?.gateway_ip_edit_text?.error = "Please enter a valid Ip"
        }

        gatewayIp = s.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}
