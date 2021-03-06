package test.demo.weatherapp.ui.weather_info.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.error_screen.*
import test.demo.weatherapp.R
import test.demo.weatherapp.ui.base.BaseFragment

class ErrorFragment : BaseFragment() {

    var listener: OnRetryListener? = null
    var errorText: String? = null

    interface OnRetryListener {
        fun onRetryClick()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.error_screen, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.setStatusBarColor(getResources().getColor(R.color.error_fragment_color));
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRetryListener)
            listener = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var text = arguments?.getString("error_msg")

        if (!text.isNullOrEmpty()) {
            tvErrorText.text = text
        }

        addRetryListener()
    }

    fun addRetryListener() {
        btnRetry.setOnClickListener {
            listener?.onRetryClick()
        }
    }

}