package im.yixin.nas.embed.demo

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import im.yixin.nas.embed.demo.adapter.MyFragmentAdapter
import im.yixin.nas.embed.demo.fragment.DemoFragment
import im.yixin.nas.embed.demo.widget.MyTabView
import im.yixin.nas.sdk.YXNasSDK
import kotlinx.android.synthetic.main.nas_demo_activity_main.*

class MainActivity : AppCompatActivity() {

    val _resources by lazy {
        return@lazy resources
    }

    val textColors = arrayOf(R.color.yx_nas_grey, R.color.yx_nas_blue_holo)
    val icons = arrayOf(
        intArrayOf(R.mipmap.ic_monitor_normal, R.mipmap.ic_monitor_sel),
        intArrayOf(R.mipmap.ic_gateway_normal, R.mipmap.ic_gateway_sel),
        intArrayOf(R.mipmap.ic_disk_normal, R.mipmap.ic_disk_sel),
        intArrayOf(R.mipmap.ic_message_normal, R.mipmap.ic_message_sel),
        intArrayOf(R.mipmap.ic_mine_normal, R.mipmap.ic_mine_sel),
    )
    val textArray = arrayOf("监控", "云网关", "智家硬盘", "消息", "我的")

    var fragments = arrayListOf<Fragment>()

    var tabs = arrayListOf<TabLayout.Tab>()

    var _initIndex = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nas_demo_activity_main)

        var index = 0
        textArray.forEach {
//            if (index == _initIndex) {
//                fragments.add(YXNasSDK.init(this))
//            } else {
//                fragments.add(DemoFragment.createDemo(it))
//            }
            fragments.add(DemoFragment.createDemo(it))
            index++
        }
        vp_fragments.offscreenPageLimit = fragments.size - 1
        vp_fragments.adapter = MyFragmentAdapter(supportFragmentManager, fragments = fragments)
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

    private fun buildIcons(normalResId: Int, selResId: Int): Array<Drawable> {
        return arrayOf(_resources.getDrawable(normalResId), _resources.getDrawable(selResId))
    }

    private fun buildTextColors(normalResId: Int, selResId: Int): Array<Int> {
        return arrayOf(_resources.getColor(normalResId), _resources.getColor(selResId))
    }
}