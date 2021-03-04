package im.yixin.nas.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import im.yixin.nas.sdk.api.INasInvokeCallback
import im.yixin.nas.sdk.api.ITokenRequestListener
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.core.NasFlutterBridgeStore
import im.yixin.nas.sdk.entity.UserInfo
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.util.LogUtil
import im.yixin.nas.sdk.util.ManifestUtil
import im.yixin.nas.sdk.util.PreConditions
import org.jetbrains.annotations.NotNull

/**
 * Created by jixia.cai on 2021/2/19 1:32 PM
 */
class YXNasSDK private constructor() : IYXNasApi {

    companion object {

        private const val NAS_SDK_APP_KEY: String = "nas_sdk_appkey"

        private const val NAS_SDK_APP_SECRET: String = "nas_sdk_appsecret"

        val TAG: String = YXNasSDK::class.java.simpleName

        @JvmStatic
        val instance = YXNasSDK()
    }

    @UiThread
    fun init(
        @NotNull app: Application,
        @NotNull appkey: String,
        @NotNull appsecret: String,
        initCallback: INasInvokeCallback<Void>?
    ): IYXNasApi {
        PreConditions.checkNotNull(app, message = "app must not be null ~")
        PreConditions.checkNotEmpty(appkey, message = "appkey must not be null ~")
        PreConditions.checkNotEmpty(appsecret, message = "appsecret must not be null ~")
        return _init(context, appkey, appsecret, initCallback)
    }

    @UiThread
    fun init(
        @NotNull app: Application,
        initCallback: INasInvokeCallback<Void>?
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
        return _init(context, appkey!!, appsecret!!, initCallback)
    }

    override fun setTokenRequestListener(listener: ITokenRequestListener?) {
        PreConditions.checkArgument(
            isInitialized,
            "YXNasSDK.setTokenRequestListener method must be called after sdk init ~"
        )
        bridgeStore?.setTokenRequestListener(listener)
    }

    private fun assertInitInvoke(condition: Boolean, method: String) {
        PreConditions.checkArgument(
            condition,
            "YXNasSDK.$method method must be called after sdk init success ~" //初始化成功之后才能调用
        )
    }

    override fun requestUserInfo(callback: INasInvokeCallback<UserInfo>?) {
        assertInitInvoke(isInitSuccess, "requestUserInfo")
        bridgeStore?.requestUserInfo(callback)
    }

    override fun requestLoginStatus(callback: INasInvokeCallback<Boolean>?) {
        assertInitInvoke(isInitSuccess, "requestLoginStatus")
        bridgeStore?.requestLoginStatus(callback)
    }

    override fun authLogin(mobile: String?, token: String?, callback: INasInvokeCallback<Void>?) {
        assertInitInvoke(isInitSuccess, "authLogin")
        bridgeStore?.authLogin(mobile, token, callback)
    }

    override fun logout(callback: INasInvokeCallback<Void>?) {
        assertInitInvoke(isInitSuccess, "logout")
        bridgeStore?.logout(callback)
    }

    var appkey: String? = null
    var appsecret: String? = null
    lateinit var context: Context

    var isInitialized = false
    var useCacheEngine = false // 缓存flutter-engine
    var bridgeStore: NasFlutterBridgeStore? = null

    var isInitSuccess = false

    private val mLogger = LogUtil.getLogger("YXNasSDK")

    private fun _init(
        context: Context,
        appkey: String,
        appsecret: String,
        initCallback: INasInvokeCallback<Void>? = null
    ): YXNasSDK {
        PreConditions.checkArgument(
            !isInitialized,
            "nas-sdk just only allow be init once ~"
        )
        mLogger.i("start init nas-sdk appkey: $appkey, appsecret: $appsecret")
        this.context = context
        this.appsecret = appsecret
        this.appkey = appkey
        buildBridgeStore().also {
            it.setNasInvokeCallback(WrapperListener(initCallback) {
                isInitSuccess = true
            })
        }
        isInitialized = true
        return this
    }

    private fun buildBridgeStore(): NasFlutterBridgeStore {
        bridgeStore = NasFlutterBridgeStore.instance.also {
            it.build(
                context,
                appkey!!,
                appsecret!!,
                useCacheEngine
            )
        }
        return bridgeStore!!
    }

    @UiThread
    override fun obtainFlutterHost(): Fragment {
        PreConditions.checkArgument(
            isInitialized,
            "YXNasSDK.obtainFlutterHost method must be called after sdk init ~"
        )
        return bridgeStore!!.obtainFlutterHost()
    }

    @UiThread
    override fun obtainFlutterIntent(): Intent {
        PreConditions.checkArgument(
            isInitialized,
            "YXNasSDK.obtainFlutterIntent method must be called after sdk init ~"
        )
        return bridgeStore!!.obtainFlutterIntent()
    }

    fun getMockApi(): INasMockApi {
        assertInitInvoke(isInitSuccess, "getMockApi")
        return bridgeStore!!
    }
}

class WrapperListener<T>(
    var listener: INasInvokeCallback<T>?,
    var onInitSuccess: InitSuccessCallback? = null
) :
    INasInvokeCallback<T> {

    override fun onResult(code: Int, message: String?, data: T?) {
        if (code == YXNasConstants.ResultCode.CODE_SUCCESS) {
            onInitSuccess?.invoke()
        }
        listener?.onResult(code, message, data)
    }

}

typealias InitSuccessCallback = () -> Unit

interface INasMockApi {

    fun mockToken(mobile: String?, callback: INasInvokeCallback<UserToken>?)
}