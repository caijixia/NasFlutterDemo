package im.yixin.nas.embed.demo

import android.app.Application
import im.yixin.nas.embed.demo.impl.NasBridgeManager
import im.yixin.nas.sdk.YXNasSDK
import im.yixin.nas.sdk.api.INasChannelBridge
import im.yixin.nas.sdk.api.INasInvokeConnector
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.SDKInitEvent
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.NasResponse
import im.yixin.nas.sdk.util.LogUtil

/**
 * Created by jixia.cai on 2021/2/22 5:16 PM
 */
class NasDemoApp : Application() {

    companion object {

        @JvmStatic
        var nasProxy: IYXNasApi? = null

        private var TAG = NasDemoApp::class.java.simpleName
    }

    private val _logger = LogUtil.getLogger(TAG)

    override fun onCreate() {
        super.onCreate()
        nasProxy = YXNasSDK.init(this, object : INasInvokeConnector {

            var bridge: INasChannelBridge? = null

            override fun onResponse(response: NasResponse) {
                val response = BaseNasResponse.parse(response)
                //处理init-event的监听
                if (response is SDKInitEvent.Response) {
                    _logger.i("init nas-sdk with result: $response")
                    NasBridgeManager.instance.notifyInitEventResponse(response) //通知初始化结果
                }
                //处理其他类型的event监听
            }

            override fun onBridgeConnected(bridge: INasChannelBridge) {
                LogUtil.i(TAG, "nas-bridge connected")
                this.bridge = bridge
                NasBridgeManager.instance.setupBridge(bridge)
            }

            override fun onBridgeDisconnected(bridge: INasChannelBridge) {
                LogUtil.i(TAG, "nas-bridge dis-connected")
                this.bridge = null
                NasBridgeManager.instance.disconnectBridge(bridge)
            }

        })
    }
}