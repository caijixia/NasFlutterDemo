package im.yixin.nas.sdk.core

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import im.yixin.nas.sdk.INasTestApi
import im.yixin.nas.sdk.activity.NasFlutterActivity
import im.yixin.nas.sdk.api.*
import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.entity.UserInfo
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.event.*
import im.yixin.nas.sdk.event.base.*
import im.yixin.nas.sdk.fragment.NasFlutterFragment
import im.yixin.nas.sdk.fragment.NasFlutterFragmentBuilder
import im.yixin.nas.sdk.util.LogUtil
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/20 11:21 AM
 */
class NasFlutterBridge : INasChannelBridge {

    private var connector: INasInvokeConnector? = null

    private var isConnected = false

    private lateinit var context: Context

    private var sink: EventChannel.EventSink? = null

    private var provider: IProvider? = null

    private var appkey: String? = null

    private var appsecret: String? = null

    private val TAG: String = NasFlutterBridge::class.java.simpleName

    private val _logger = LogUtil.getLogger(YXNasConstants.TAG)

    private val _listeners = mutableMapOf<String, NasFlutterCallbackListener<*>>()

    private val mMainHandler = Handler(Looper.getMainLooper())

    fun build(context: Context, provider: IProvider?) {
        this.provider = provider
        appkey = provider?.provideAppConf()?.appkey
        appsecret = provider?.provideAppConf()?.appsecret
        this.connector = provider?.provideConnector()
        this.context = context
    }

    fun setup(sink: EventChannel.EventSink?) {
        this.sink = sink
    }

    private fun <T> notifyFlutterRequest(request: NasRequest, methodCall: IMethodCall<T>) {
        runInMainThread {
            connector?.onRequest(request, methodCall)
        }
    }

    fun handleMethodCall(call: MethodCall?, result: MethodChannel.Result?) {
        _logger.i("receive flutter-call with: ${YXNasConstants.toJson(call)}")
        if (call == null) return
        val response = BaseNasResponse.parse(NasResponse(call.method, call.arguments))
        _logger.i("parse flutter response: ${YXNasConstants.toJson(response)}")
        if (response is SDKInitEvent.Response) {
            val listener = removeCallBackListener(response.method!!)
            if (listener != null) {
                notifyNasInvokeResult(
                    response.result,
                    listener.callback as INasInvokeCallback<Void>
                )
            }
            //通知flutter 调用成功
            notifyFlutter(
                result,
                FlutterCallbackResult()
            )
        }
        //判断request_token类型
        else if (response is TokenRequestEvent.Request) {
            val request = NasRequest(response.method!!)
            val methodCall = FlutterMethodCall<UserToken>(result)
            notifyFlutterRequest(request, methodCall)
        } else {
            val method: String? = call.method
            if (method != null) {
                val listener = removeCallBackListener(call.method)
                if (listener != null) {
                    notifyNasInvokeResult(
                        response.result as Result<Any>?,
                        listener.callback as INasInvokeCallback<Any>?
                    )
                }
                //通知flutter 调用成功
                notifyFlutter(
                    result,
                    FlutterCallbackResult()
                )
            } else {
                result?.notImplemented()
            }
        }
    }

    private fun notifyFlutter(result: MethodChannel.Result?, content: FlutterCallbackResult) {
        result?.success(content.toJSON().toString())
    }

    fun startConnect() {
        notifyConnect()
        //初始化event特殊处理，通过 onResponse 方法回调
        callFlutter(buildInitRequest(), object : INasInvokeCallback<Void> {

            override fun onResult(code: Int, message: String?, data: Void?) {
                notifyNasResult(SDKInitEvent.Response.build(code, message).bundle)
            }
        })
    }

    //通过eventChannel调用flutter
    private fun <T> callFlutter(request: BaseNasRequest, callback: INasInvokeCallback<T>?) {
        //检查request类型&参数，非法则直接回调
        val checkRet = request.preCheck()
        if (checkRet != null && !checkRet.success) {
            callback?.onResult(YXNasConstants.ResultCode.CODE_BAD_REQUEST, checkRet.message, null)
            return
        }
        addCallbackListener(request.method, callback) //添加回调监听器
        sink?.success(request.toJSON().toString()) //执行flutter调用，统一转换成json
    }

    private fun buildInitRequest(): SDKInitEvent.Request {
        return SDKInitEvent.Request(
            appkey = appkey!!, appsecret = appsecret!!
        )
    }

    private fun notifyConnect() {
        connector?.onBridgeConnected(this)
        isConnected = true
    }

    fun notifyDisconnect() {
        if (isConnected) {
            connector?.onBridgeDisconnected(this)
            isConnected = false
        }
    }

    override fun <T> invoke(request: BaseNasRequest, callback: INasInvokeCallback<T>?) {
        LogUtil.i(YXNasConstants.TAG, "invoke flutter ${YXNasConstants.toJson(request)}")
        callFlutter(request, callback)
    }

    private fun <T> addCallbackListener(method: String, callback: INasInvokeCallback<T>?) {
        if (callback == null) return
        if (_listeners.containsKey(method)) {
            _listeners.remove(method)
        }
        _listeners[method] = NasFlutterCallbackListener.newBuilder(callback)
    }

    private fun removeCallBackListener(method: String): NasFlutterCallbackListener<*>? {
        return _listeners.remove(method)
    }

    private fun <T : Any> notifyNasInvokeResult(
        result: Result<T>?,
        callback: INasInvokeCallback<T>?
    ) {
        if (callback == null || result == null) return
        //主线程回调
        runInMainThread {
            callback.onResult(result.code, result.message, result.data)
        }
    }

    private fun notifyNasResult(response: NasResponse) {
        //主线程回调
        runInMainThread {
            connector?.onResponse(response)
        }
    }

    private fun runInMainThread(runnable: Runnable?) {
        if (runnable != null) {
            mMainHandler.post {
                runnable.run()
            }
        }
    }
}

class NasFlutterBridgeStore private constructor() : IFactory<Void, NasFlutterBridge>,
    IProvider, INasTestApi, IYXNasApi {

    lateinit var _engine: NasFlutterEngine

    lateinit var context: Context

    var appkey: String? = null

    var appsecret: String? = null

    private var useCacheEngine: Boolean = true

    private var _initCallback: INasInvokeCallback<Void>? = null

    private var _tokenRequestListener: ITokenRequestListener? = null

    private var bridge: INasChannelBridge? = null

    private var _connector = object : INasInvokeConnector {
        override fun onResponse(response: NasResponse) {
            val result = response.result
            when (response.method) {
                YXNasConstants.Method.EVENT_METHOD_SDK_INIT -> {
                    if (result is Result<*>) {
                        _initCallback?.onResult(result.code, result.message, result.data as Void?)
                    }
                }
            }
        }

        override fun onRequest(request: NasRequest, methodCall: IMethodCall<*>?) {
            when (request.method) {
                YXNasConstants.Method.EVENT_METHOD_TOKEN_REQUEST -> {
                    _tokenRequestListener?.onTokenRequest(methodCall as IMethodCall<UserToken>)
                }
            }
        }

        override fun onBridgeConnected(bridge: INasChannelBridge) {
            this@NasFlutterBridgeStore.bridge = bridge
        }

        override fun onBridgeDisconnected(bridge: INasChannelBridge) {
            this@NasFlutterBridgeStore.bridge = null
        }
    }

    companion object {

        val instance = NasFlutterBridgeStore()
    }

    fun build(
        context: Context,
        appkey: String,
        appsecret: String,
        useCacheEngine: Boolean
    ) {
        this.context = context
        this.appkey = appkey
        this.appsecret = appsecret
        this.useCacheEngine = useCacheEngine
        if (useCacheEngine) {
            _engine = NasFlutterEngine().also {
                it.preWarm(context)
            }
        }
    }

    override fun produce(avoid: Void?): NasFlutterBridge {
        return NasFlutterBridge().also {
            it.build(context, this)
        }
    }

    override fun provideAppConf(): AppConf = AppConf(appkey!!, appsecret!!)

    override fun provideConnector(): INasInvokeConnector? = _connector

    override fun mockToken(mobile: String?, callback: INasInvokeCallback<UserToken>?) {
        bridge?.invoke(MockTokenGetEvent.Request(mobile), callback)
    }

    override fun obtainFlutterHost(): Fragment {
        return if (useCacheEngine) {
            NasFlutterFragmentBuilder(_engine.engineId).build()
        } else {
            FlutterFragment.NewEngineFragmentBuilder(NasFlutterFragment::class.java).build()
        }
    }

    override fun obtainFlutterIntent(): Intent {
        return if (useCacheEngine) {
            FlutterActivity.CachedEngineIntentBuilder(
                NasFlutterActivity::class.java,
                _engine.engineId
            ).build(context)
        } else {
            FlutterActivity.NewEngineIntentBuilder(NasFlutterActivity::class.java).build(context)
        }
    }

    fun setNasInvokeCallback(callback: INasInvokeCallback<Void>?) {
        _initCallback = callback
    }

    override fun setTokenRequestListener(listener: ITokenRequestListener?) {
        _tokenRequestListener = listener
    }

    override fun requestUserInfo(callback: INasInvokeCallback<UserInfo>?) {
        bridge?.invoke(GetUserInfoEvent.Request(), callback = callback)
    }

    override fun requestLoginStatus(callback: INasInvokeCallback<Boolean>?) {
        bridge?.invoke(GetUserStatusEvent.Request(), callback)
    }

    override fun authLogin(mobile: String?, token: String?, callback: INasInvokeCallback<Void>?) {
        bridge?.invoke(UserAuthEvent.Request(mobile, token), callback)
    }

    override fun logout(callback: INasInvokeCallback<Void>?) {
        bridge?.invoke(UserLogoutEvent.Request(), callback)
    }
}

interface IProvider {

    fun provideAppConf(): AppConf?

    fun provideConnector(): INasInvokeConnector?
}

data class AppConf(val appkey: String, val appsecret: String)

interface IFactory<T, R> {

    fun produce(param: T? = null): R
}

class NasFlutterCallbackListener<T> private constructor() {

    companion object {

        fun <T> newBuilder(callback: INasInvokeCallback<T>?): NasFlutterCallbackListener<T> {
            return NasFlutterCallbackListener<T>().also {
                it.callback = callback
            }
        }
    }

    var callback: INasInvokeCallback<T>? = null
        private set
}

class FlutterCallbackResult {

    var code: Int? = YXNasConstants.ResultCode.CODE_SUCCESS
    var message: String? = null

    constructor(code: Int? = YXNasConstants.ResultCode.CODE_SUCCESS, message: String? = null) {
        this.code = code
        this.message = message
    }

    fun success(): Boolean = code == YXNasConstants.ResultCode.CODE_SUCCESS

    fun toJSON(): JSONObject {
        return JSONObject().also {
            it.put("code", code)
            it.put("message", message)
        }
    }
}

class FlutterMethodCall<T> : IMethodCall<T> {

    var _result: MethodChannel.Result? = null

    constructor(result: MethodChannel.Result?) {
        this._result = result
    }

    override fun success(result: T?) {
        val ret = Result.buildSuccess(result.toString()).toJSON().toString()
        LogUtil.i(YXNasConstants.TAG, "success result: $ret")
        _result?.success(ret)
    }

    override fun error(code: Int, message: String?) {
        val result = Result.build<Void>(code, message)
        _result?.success(result.toJSON().toString())
    }

}