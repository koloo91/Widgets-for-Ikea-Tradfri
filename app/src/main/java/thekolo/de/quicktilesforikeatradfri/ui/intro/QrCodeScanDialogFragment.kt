package thekolo.de.quicktilesforikeatradfri.ui.intro

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.fragment_qr_code_scan_dialog.view.*
import thekolo.de.quicktilesforikeatradfri.R

class QrCodeScanDialogFragment : DialogFragment() {
    var listener: OnCodeScannedListener? = null

    private var detector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_qr_code_scan_dialog, container, false)

        detector = BarcodeDetector.Builder(activity?.applicationContext)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build()

        if (!detector!!.isOperational) {
            println("Could not set up the detector!")
            return view
        }

        cameraSource = CameraSource.Builder(context, detector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 640)
                .build()

        view?.surface_view?.holder?.addCallback(surfaceHolderCallback)

        detector?.setProcessor(detectorProcessor)

        return view
    }

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            cameraSource?.stop()
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            try {
                cameraSource?.start(view?.surface_view!!.holder)
            } catch (e: SecurityException) {

            }
        }
    }

    private val detectorProcessor = object : Detector.Processor<Barcode> {
        override fun release() {}

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            val detectedItems = detections.detectedItems
            if (detectedItems.size() == 0) return

            view?.post {
                val securityCode = detectedItems.valueAt(0).displayValue
                println(securityCode)
                listener?.onCodeScanned(securityCode)

                cameraSource?.stop()

                detector?.release()
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(): QrCodeScanDialogFragment {
            return QrCodeScanDialogFragment()
        }
    }

    interface OnCodeScannedListener {
        fun onCodeScanned(code: String)
    }
}