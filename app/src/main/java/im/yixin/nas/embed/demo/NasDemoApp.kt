package im.yixin.nas.embed.demo

import android.app.Application
import im.yixin.nas.sdk.INasChannelBridge
import im.yixin.nas.sdk.INasInvokeConnector
import im.yixin.nas.sdk.NasBundle
import im.yixin.nas.sdk.YXNasSDK
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.event.AuthEvent
import im.yixin.nas.sdk.event.convert.InitEventBundle
import im.yixin.nas.sdk.util.LogUtil

/**
 * Created by jixia.cai on 2021/2/22 5:16 PM
 */
class NasDemoApp : Application() {

    companion object {

        @JvmStatic
        var sNasProxy: IYXNasApi? = null

        private var TAG = NasDemoApp::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        sNasProxy = YXNasSDK.init(this, object : INasInvokeConnector {

            var bridge: INasChannelBridge? = null

            override fun onBundleReceived(bundle: NasBundle) {
                val eventBundle = NasBundle.parse(bundle)
                if (eventBundle is InitEventBundle) {
                    LogUtil.i(TAG, "init sdk with result: $eventBundle")
                    if (eventBundle.success()) {
                        //省略获取token的过程， 或者可以通过底层flutter去模拟
                        bridge?.invoke(
                            AuthEvent(mobile = "15988114226", token = "10001").toBundle(),
                        )
                    }
                }
            }

            override fun onBridgeConnected(bridge: INasChannelBridge) {
                LogUtil.i(TAG, "nas-bridge connected")
                this.bridge = bridge
            }

            override fun onBridgeDisconnected(bridge: INasChannelBridge) {
                LogUtil.i(TAG, "nas-bridge dis-connected")
                this.bridge = null
            }

        })
    }
}