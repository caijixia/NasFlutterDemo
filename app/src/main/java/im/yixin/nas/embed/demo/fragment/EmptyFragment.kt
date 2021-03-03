package im.yixin.nas.embed.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import im.yixin.nas.embed.demo.R

/**
 * Created by jixia.cai on 2021/3/1 8:17 PM
 */
class EmptyFragment(private var title: String? = null, private var message: String? = null) :
    Fragment(R.layout.nas_demo_fragment_empty) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val toolbar = view!!.findViewById<Toolbar>(R.id.toolbar)
        val tv_message = view.findViewById<TextView>(R.id.tv_message)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toolbar.title = title ?: "标题"
        }
        tv_message.text = message ?: "页面内容"
        return view
    }
}