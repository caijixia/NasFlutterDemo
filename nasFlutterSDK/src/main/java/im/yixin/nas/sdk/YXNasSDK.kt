package im.yixin.nas.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.core.NasFlutterBridgeStore
import im.yixin.nas.sdk.event.BasicNasEvent
import im.yixin.nas.sdk.event.callback.VoidResult
import im.yixin.nas.sdk.event.convert.EventBundle
import im.yixin.nas.sdk.event.convert.InitEventBundle
import im.yixin.nas.sdk.fragment.NasFlutterFragment
import im.yixin.nas.sdk.util.LogUtil
import im.yixin.nas.sdk.util.ManifestUtil
import im.yixin.nas.sdk.util.ParseUtil
import im.yixin.nas.sdk.util.PreConditions
import io.flutter.embedding.android.FlutterFragment
import org.jetbrains.annotations.NotNull
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/19 1:32 PM
 */
class YXNasSDK private constructor() : IYXNasApi {

    companion object {

        private const val NAS_SDK_APP_KEY: String = "nas_sdk_appkey"

        private const val NAS_SDK_APP_SECRET: String = "nas_sdk_appsecret"

        val TAG: String = YXNasSDK::class.java.simpleName

        private var context: Context? = null

        @UiThread
        fun init(
            @NotNull app: Application,
            @NotNull appkey: String,
            @NotNull appsecret: String,
            connector: INasInvokeConnector? = null
        ): IYXNasApi {
            PreConditions.checkNotNull(app, message = "app must not be null ~")
            PreConditions.checkNotEmpty(appkey, message = "appkey must not be null ~")
            PreConditions.checkNotEmpty(appsecret, message = "appsecret must not be null ~")
            return instance.init(context!!, appkey, appsecret).also {
                it.connector = connector
            }
        }

        @UiThread
        fun init(
            @NotNull app: Application,
            connector: INasInvokeConnector? = null
        ): IYXNasApi {
            PreConditions.checkNotNull(app, message = "app must not be null ~")
            context = app.applicationContext
            val appkey = ManifestUtil.readMetaInfo(context!!, NAS_SDK_APP_KEY)
            val appsecret = ManifestUtil.readMetaInfo(context!!, NAS_SDK_APP_SECRET)
            LogUtil.i(TAG, "int nas-sdk with appkey: $appkey, appsecret: $appsecret")

            PreConditions.checkNotEmpty(
                appkey,
                message = "meta-data of $NAS_SDK_APP_KEY in mainifest.xml must not be null ~"
            )
            PreConditions.checkNotEmpty(
                appsecret,
                message = "meta-data of $NAS_SDK_APP_SECRET in mainifest.xml  must not be null ~"
            )
            return instance.init(context!!, appkey!!, appsecret!!).also {
                it.connector = connector
                it.buildBridgeStore()
            }
        }

        private val instance = YXNasSDK()
    }

    var appkey: String? = null
    var appsecret: String? = null
    var connector: INasInvokeConnector? = null
    lateinit var context: Context

    var isInitialized = false
    var useCacheEngine = true // 缓存flutter-engine
    var bridgeStore: NasFlutterBridgeStore? = null

    private val mLogger = LogUtil.getLogger("YXNasSDK")

    private fun init(context: Context, appkey: String, appsecret: String): YXNasSDK {
        PreConditions.checkArgument(
            !isInitialized,
            "nas-sdk just only allow be init once ~"
        )
        mLogger.i("start init nas-sdk appkey: $appkey, appsecret: $appsecret")
        this.context = context
        this.appsecret = appsecret
        this.appkey = appkey
        isInitialized = true
        return this
    }

    private fun buildBridgeStore() {
        bridgeStore = NasFlutterBridgeStore.instance.also {
            it.build(
                context,
                appkey!!,
                appsecret!!,
                useCacheEngine,
                connector
            )
        }
    }

    @UiThread
    override fun obtainFlutterHost(): Fragment {
        return bridgeStore!!.obtainFlutterHost()
    }

    override fun obtainFlutterIntent(): Intent {
        return bridgeStore!!.obtainFlutterIntent()
    }
}

interface INasInvokeConnector {

    fun onBundleReceived(bundle: NasBundle)

    fun onBridgeConnected(bridge: INasChannelBridge)

    fun onBridgeDisconnected(bridge: INasChannelBridge)
}

interface INasChannelBridge {

    fun invoke(bundle: NasBundle, callback: INasCallback? = null)

    @Deprecated("")
    fun broadEvent(event: NasEvent, args: Any?)

    @Deprecated("")
    fun fireEvent(event: NasEvent, args: Any?, callback: INasCallback? = null)
}

enum class NasEvent

data class NasBundle(val method: String, val args: Any? = null) {

    fun isVerify(): Boolean = !method?.isNullOrEmpty()

    companion object {

        fun parse(bundle: NasBundle): EventBundle? {
            when (bundle.method) {
                NasMethodConst.EVENT_METHOD_SDK_INIT -> return InitEventBundle(
                    ParseUtil.parseEventResult<VoidResult>(
                        bundle.args
                    )
                )
            }
            return null
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}

object NasMethodConst {

    const val EVENT_NAME_BRIDGE_CONNECT = "connect"

    const val EVENT_NAME_BRIDGE_DISCONNECT = "disconnect"

    const val EVENT_METHOD_SDK_INIT = "sdk_init"

    const val EVENT_METHOD_AUTH = "auth"

    const val EVENT_METHOD_LOGOUT = "logout"

    const val EVENT_METHOD_SWITCH = "switch"

    //其他sdk 交互 event 待补充，如：获取登录态/登录信息等
}

@Deprecated("")
object NasCallbackConst {

    const val EVENT_METHOD_SDK_INIT = "callback_sdk_init"

    const val EVENT_CALLBACK_AUTH = "callback_auth"

    const val EVENT_CALLBACK_LOGOUT = "callback_logout"

    const val EVENT_CALLBACK_ACCOUNT_SWITCH = "callback_account_switch"
}

interface INasCallback {

    fun onSuccess(data: Any? = null) //map或者JSONOject类型

    fun onError(code: Int, message: String?)
}

object NasResultCode {

    const val CODE_SUCCESS = 200

    const val CODE_BAD_REQUEST = 400 //参数错误

    const val CODE_INTERRUPT = 4001 //调用中断
}