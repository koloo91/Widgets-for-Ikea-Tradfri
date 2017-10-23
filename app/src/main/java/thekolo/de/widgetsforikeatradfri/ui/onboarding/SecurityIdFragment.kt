package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_scan_security_id.view.*
import thekolo.de.widgetsforikeatradfri.R


class SecurityIdFragment : Fragment() {

    var listener: OnSecurityIdFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_scan_security_id, container, false)
        view.finish_button.setOnClickListener {
            listener?.onSecurityIdFragmentFinishClicked()
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
