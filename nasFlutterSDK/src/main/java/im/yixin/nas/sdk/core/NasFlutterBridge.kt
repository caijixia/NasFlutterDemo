package im.yixin.nas.sdk.core

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import im.yixin.nas.sdk.activity.NasFlutterActivity
import im.yixin.nas.sdk.api.INasCallback
import im.yixin.nas.sdk.api.INasChannelBridge
import im.yixin.nas.sdk.api.INasInvokeConnector
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.SDKInitEvent
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.NasResponse
import im.yixin.nas.sdk.event.base.Result
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

    private val _logger = LogUtil.getLogger(YXNasConstants.compose(TAG))

    private val _listeners = mutableMapOf<String, NasFlutterCallbackListener>()

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

    fun handleMethodCall(call: MethodCall?, result: MethodChannel.Result?) {
        if (call == null) return
        val response = BaseNasResponse.parse(NasResponse(call.method, call.arguments))
        if (response is SDKInitEvent.Response) {
            val listener = removeCallBackListener(response.method!!)
            if (listener != null) {
                notifyNasInvokeResult(response.result, listener.callback)
            }
            //通知flutter 调用成功
            notifyFlutter(
                result,
                FlutterCallbackResult()
            )
        } else {
            val method: String? = call.method
            if (method != null) {
                val listener = removeCallBackListener(call.method)
                if (listener != null) {
                    notifyNasInvokeResult(response.result, listener.callback)
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
        result?.success(content.toJSON())
    }

    fun startConnect() {
        notifyConnect()
        //初始化event特殊处理，通过 onResponse 方法回调
        callFlutter(buildInitRequest(), object : INasCallback {

            override fun onSuccess(data: Any?) {
                notifyNasResult(SDKInitEvent.Response.buildSuccess().bundle)
            }

            override fun onError(code: Int, message: String?) {
                notifyNasResult(SDKInitEvent.Response.build(code, message).bundle)
            }
        })
    }

    //通过eventChannel调用flutter
    private fun callFlutter(request: BaseNasRequest, callback: INasCallback?) {
        //检查request类型&参数，非法则直接回调
        val checkRet = request.preCheck()
        if (checkRet != null && !checkRet.success) {
            callback?.onError(YXNasConstants.ResultCode.CODE_BAD_REQUEST, checkRet.message)
            return
        }
        addCallbackListener(request.method, callback) //添加回调监听器
        sink?.success(request.toJSON()) //执行flutter调用，统一转换成JSONObject
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

    override fun invoke(request: BaseNasRequest, callback: INasCallback?) {
        callFlutter(request, callback)
    }

    private fun addCallbackListener(method: String, callback: INasCallback?) {
        if (callback == null) return
        if (_listeners.containsKey(method)) {
            _listeners.remove(method)
        }
        _listeners[method] = NasFlutterCallbackListener.newBuilder(callback)
    }

    private fun removeCallBackListener(method: String): NasFlutterCallbackListener? {
        return _listeners.remove(method)
    }

    private fun notifyNasInvokeResult(result: Result<*>?, callback: INasCallback?) {
        if (callback == null || result == null) return
        //主线程回调
        runInMainThread {
            if (result.success()) {
                callback.onSuccess(result.data)
            } else {
                callback.onError(result.code, result.message)
            }
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
    IProvider, IYXNasApi {

    lateinit var _engine: NasFlutterEngine

    lateinit var context: Context

    var appkey: String? = null

    var appsecret: String? = null

    private var useCacheEngine: Boolean = true

    var connector: INasInvokeConnector? = null

    companion object {

        val instance = NasFlutterBridgeStore()
    }

    fun build(
        context: Context,
        appkey: String,
        appsecret: String,
        useCacheEngine: Boolean,
        connector: INasInvokeConnector?
    ) {
        this.context = context
        this.appkey = appkey
        this.appsecret = appsecret
        this.connector = connector
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

    override fun provideConnector(): INasInvokeConnector? = connector

    override fun obtainFlutterHost(): Fragment {
        return if (useCacheEngine) {
            NasFlutterFragmentBuilder(_engine.engineId).build()
        } else {
            FlutterFragment.withNewEngine().build()
        }
    }

    override fun obtainFlutterIntent(): Intent {
        return if (useCacheEngine) {
            FlutterActivity.CachedEngineIntentBuilder(
                NasFlutterActivity::class.java,
                _engine.engineId
            ).build(context)
        } else {
            FlutterActivity.withNewEngine().build(context)
        }
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

class NasFlutterCallbackListener private constructor() {

    companion object {

        fun newBuilder(callback: INasCallback?): NasFlutterCallbackListener {
            return NasFlutterCallbackListener().also {
                it.callback = callback
            }
        }
    }

    var callback: INasCallback? = null
        private set
}

class FlutterCallbackResult {

    var code: Int? = YXNasConstants.ResultCode.CODE_SUCCESS
    var message: String? = null

    constructor(code: Int? = null, message: String? = null) {
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