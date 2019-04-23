package thekolo.de.quicktilesforikeatradfri.ui.intro


import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_try_register.view.*
import thekolo.de.quicktilesforikeatradfri.R
import thekolo.de.quicktilesforikeatradfri.tradfri.TradfriService
import thekolo.de.quicktilesforikeatradfri.utils.SettingsUtil
import java.util.*


class TryRegisterFragment : SlideFragment() {

    private var canMoveFurther = false
    private var currentErrorMessage = "Test your connection before proceeding"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_try_register, container, false)
        view.progress_bar.visibility = View.INVISIBLE
        return view
    }

    override fun buttonsColor(): Int {
        return R.color.colorAccent
    }

    override fun backgroundColor(): Int {
        return R.color.colorPrimary
    }

    override fun canMoveFurther(): Boolean {
        return canMoveFurther
    }

    override fun cantMoveFurtherErrorMessage(): String {
        return currentErrorMessage
    }

    fun tryRegister() {
        view?.progress_bar?.visibility = View.VISIBLE

        val identity = "${UUID.randomUUID()}".replace("-", "").toLowerCase()

        TradfriService.instance(activity!!.applicationContext).refreshClient(activity!!.applicationContext)
        TradfriService.instance(activity!!.applicationContext).register(identity, { registerResult ->
            SettingsUtil.setIdentity(activity!!.applicationContext, identity)
            SettingsUtil.setPreSharedKey(activity!!.applicationContext, registerResult.preSharedKey)

            canMoveFurther = true
            view?.progress_bar?.visibility = View.INVISIBLE
            (activity as MaterialIntroActivity).showMessage("Connection was successful")
        }, {
            view?.progress_bar?.visibility = View.INVISIBLE
            (activity as MaterialIntroActivity).showMessage("Connection was not successful. Please check your data again")
            currentErrorMessage = "Your data does not seem to be correct"
        })
    }
}
