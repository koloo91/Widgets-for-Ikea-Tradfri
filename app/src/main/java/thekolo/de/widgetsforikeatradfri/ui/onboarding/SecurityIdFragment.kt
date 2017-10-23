package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_scan_security_id.view.*
import thekolo.de.widgetsforikeatradfri.R
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector


class SecurityIdFragment : Fragment() {

    var listener: OnSecurityIdFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan_security_id, container, false)
        view.finish_button.setOnClickListener {
            listener?.onSecurityIdFragmentFinishClicked()
        }
        view.scan_qr_code_button.setOnClickListener {
            val detector = BarcodeDetector.Builder(activity.applicationContext)
                    .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE)
                    .build()

            if (!detector.isOperational) {
                println("Could not set up the detector!")
                return@setOnClickListener
            }


        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSecurityIdFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnSecurityIdFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSecurityIdFragmentInteractionListener {
        fun onSecurityIdFragmentFinishClicked()
    }

    companion object {
        fun newInstance(): SecurityIdFragment {
            return SecurityIdFragment()
        }
    }
}
