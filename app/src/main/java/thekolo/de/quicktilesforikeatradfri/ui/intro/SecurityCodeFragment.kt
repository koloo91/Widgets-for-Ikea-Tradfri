package thekolo.de.quicktilesforikeatradfri.ui.intro


import agency.tango.materialintroscreen.SlideFragment
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_security_code.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil


class SecurityCodeFragment : SlideFragment(), QrCodeScanDialogFragment.OnCodeScannedListener, TextWatcher {

    var securityCode = ""
        set(value) {
            Log.d("SecurityCodeFragment", "Setting security code $value")
            SettingsUtil.setSecurityId(activity!!.applicationContext, value)
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_security_code, container, false)
        view.security_code_edit_text.addTextChangedListener(this)
        return view
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun backgroundColor(): Int {
        return R.color.colorPrimary
    }

    override fun canMoveFurther(): Boolean {
        return securityCode.isNotEmpty()
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return "Please enter your gateways security code!"
    }

    fun scanSecurityCode() {
        checkForCameraPermission()
    }

    private fun showScanDialog() {
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val previousFragment = fragmentManager!!.findFragmentByTag("qr_scan")
        if (previousFragment != null) {
            fragmentTransaction.remove(previousFragment)
        }
        fragmentTransaction.addToBackStack(null)

        // Create and show the dialog.
        val newFragment = QrCodeScanDialogFragment.newInstance()
        newFragment.listener = this
        newFragment.show(fragmentTransaction, "qr_scan")
    }

    private fun checkForCameraPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {

            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
            }
        } else showScanDialog()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                return
            }

            showScanDialog()
        }
    }

    override fun onCodeScanned(scanResult: String) {
        var code = scanResult
        if(code.contains(","))
            code = code.split(",").last()

        view?.security_code_edit_text?.setText(code)
        securityCode = code
    }

    override fun afterTextChanged(s: Editable?) {
        securityCode = s.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    companion object {
        private const val PERMISSION_REQUEST_CAMERA = 808
    }
}
