package im.yixin.nas.embed.demo.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import im.yixin.nas.embed.demo.NasDemoApp
import im.yixin.nas.embed.demo.R
import im.yixin.nas.embed.demo.adapter.MyFragmentAdapter
import im.yixin.nas.embed.demo.fragment.EmptyFragment
import im.yixin.nas.embed.demo.fragment.TabItemFragment
import im.yixin.nas.embed.demo.impl.NasInvocationProxy
import im.yixin.nas.embed.demo.impl.OnNasSDKnitListener
import im.yixin.nas.embed.demo.impl.OnNasUserListener
import im.yixin.nas.embed.demo.widget.MyTabView
import im.yixin.nas.sdk.YXNasSDK
import im.yixin.nas.sdk.api.INasInvokeCallback
import im.yixin.nas.sdk.const.YXNasConstants
import kotlinx.android.synthetic.main.nas_demo_activity_tab_mode.*

class TabModeActivity : AppCompatActivity() {

    private val textColors = arrayOf(R.color.yx_nas_grey, R.color.yx_nas_blue_holo)
    private val icons = arrayOf(
        intArrayOf(R.mipmap.ic_monitor_normal, R.mipmap.ic_monitor_sel),
        intArrayOf(R.mipmap.ic_gateway_normal, R.mipmap.ic_gateway_sel),
        intArrayOf(R.mipmap.ic_disk_normal, R.mipmap.ic_disk_sel),
        intArrayOf(R.mipmap.ic_message_normal, R.mipmap.ic_message_sel),
        intArrayOf(R.mipmap.ic_mine_normal, R.mipmap.ic_mine_sel),
    )
    private val textArray = arrayOf("监控", "云网关", "智家硬盘", "消息", "我的")

    private var fragments = arrayListOf<Fragment>()

    private var tabs = arrayListOf<TabLayout.Tab>()

    private var _initIndex = 2

    private val emptyFragment = EmptyFragment()

    private var pageAdapter: MyFragmentAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nas_demo_activity_tab_mode)

        var index = 0
        textArray.forEach {
            if (index == _initIndex) {
                fragments.add(YXNasSDK.instance.obtainFlutterHost())
            } else if (index == textArray.size - 1) {
                fragments.add(emptyFragment) //显示空页面
            } else {
                fragments.add(TabItemFragment.createDemo(it))
            }
            index++
        }
        vp_fragments.offscreenPageLimit = fragments.size - 1
        pageAdapter = MyFragmentAdapter(supportFragmentManager, fragments = fragments)
        vp_fragments.adapter = pageAdapter
        ly_tabs.setupWithViewPager(vp_fragments)
        val size = ly_tabs.tabCount
        for (i in 0 until size) {
            MyTabView.obtainNew(
                icons = buildIcons(icons[i][0], icons[i][1]),
                textColors = buildTextColors(textColors[0], textColors[1]),
                text = textArray[i]
            ).applyCustomView(ly_tabs.getTabAt(i)!!)
            tabs.add(ly_tabs.getTabAt(i)!!)
        }
        ly_tabs.selectTab(tabs[_initIndex])
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun buildIcons(normalResId: Int, selResId: Int): Array<Drawable> {
        return arrayOf(resources.getDrawable(normalResId), resources.getDrawable(selResId))
    }

    private fun buildTextColors(normalResId: Int, selResId: Int): Array<Int> {
        return arrayOf(resources.getColor(normalResId), resources.getColor(selResId))
    }

}