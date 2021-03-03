package im.yixin.nas.embed.demo.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import im.yixin.nas.embed.demo.R

/**
 * Created by jixia.cai on 2021/2/19 2:00 PM
 */
class TabItemFragment(private val title: String?) : Fragment(R.layout.nas_demo_fragment) {

    companion object {

        fun createDemo(title: String): TabItemFragment {
            return TabItemFragment(title)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            initViews(view)
        }
        return view
    }

    fun initViews(view: View) {
        val tv_content = view.findViewById<TextView>(R.id.tv_content)
        tv_content.text = aheadTitle()
    }

    fun aheadTitle(): CharSequence {
        val builder = SpannableStringBuilder("$title，敬请期待～")
        builder.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            title?.length ?: 0,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return builder
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}