package thekolo.de.widgetsforikeatradfri.ui.onboarding


import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_scan_security_id.*
import kotlinx.android.synthetic.main.fragment_scan_security_id.view.*
import thekolo.de.widgetsforikeatradfri.R
import thekolo.de.widgetsforikeatradfri.utils.SettingsUtil


class SecurityIdFragment : Fragment() {

    private var cameraSource: CameraSource? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan_security_id, container, false)

        SettingsUtil.setSecurityId(context, view.security_id_edit_text.text.toString())
        view.scan_qr_code_button.setOnClickListener(onScanButtonClicked)

        return view
    }

    private val onScanButtonClicked = View.OnClickListener {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        onPermissionGranted()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                    }

                })
                .check()
    }

    @SuppressLint("MissingPermission")
    private fun onPermissionGranted() {
        view?.camera_suraface_view?.visibility = View.VISIBLE

        val detector = BarcodeDetector.Builder(activity.applicationContext)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()

        if (!detector.isOperational) {
            println("Could not set up the detector!")
            return
        }

        cameraSource = CameraSource.Builder(context, detector)
                .setAutoFocusEnabled(true)
                .build()

        try {
            cameraSource?.start(camera_suraface_view.holder)
        } catch (e: Exception) {
            println(e)
            return
        }

        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val detectedItems = detections.detectedItems
                if (detectedItems.size() == 0) return

                view?.post {
                    println(detectedItems.valueAt(0).displayValue)
                    view?.security_id_edit_text?.setText(detectedItems.valueAt(0).displayValue)
                    view?.camera_suraface_view?.visibility = View.INVISIBLE
                }

                cameraSource?.stop()
            }
        })
    }

    companion object {
        fun newInstance(): SecurityIdFragment {
            return SecurityIdFragment()
        }
    }
}
