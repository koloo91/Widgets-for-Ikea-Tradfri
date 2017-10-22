package thekolo.de.widgetsforikeatradfri.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_welcome.view.*
import thekolo.de.widgetsforikeatradfri.R


class WelcomeFragment : Fragment() {

    var listener: OnWelcomeFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_welcome, container, false)

        view.next_button.setOnClickListener {
            listener?.onWelcomeFragmentNextButtonClicked()
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnWelcomeFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnWelcomeFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnWelcomeFragmentInteractionListener {
        fun onWelcomeFragmentNextButtonClicked()
    }

    companion object {
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }
}
