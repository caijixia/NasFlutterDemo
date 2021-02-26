package im.yixin.nas.sdk.core

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import im.yixin.nas.sdk.*
import im.yixin.nas.sdk.api.IYXNasApi
import im.yixin.nas.sdk.event.callback.BasicEventResult
import im.yixin.nas.sdk.event.callback.VoidResult
import im.yixin.nas.sdk.event.convert.InitEventBundle
import im.yixin.nas.sdk.fragment.NasFlutterFragmentBuilder
import im.yixin.nas.sdk.util.LogUtil
import im.yixin.nas.sdk.util.ParseUtil
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

    private var mGson: Gson = GsonBuilder().create()

    private val TAG: String = NasFlutterBridge::class.java.simpleName

    private val _logger = LogUtil.getLogger(TAG)

    private val _listeners = mutableMapOf<String, NasFlutterCallbackListener>()

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
        when (val method = call.method) {
            NasMethodConst.EVENT_METHOD_SDK_INIT -> {
                //通知 调用方 初始化的结果
                notifyNasResult(NasBundle(method, call.arguments))
                result?.success(null) //通知flutter调用成功
            }
            else -> {
                //查找callbackListener,通过 listener 进行回调
                val listener = _listeners[method]
                if (listener != null) {
                    //授权登录回调
                    if (method == NasMethodConst.EVENT_METHOD_AUTH) { //授权登录没有返回data
                        val ret = ParseUtil.parseEventResult<VoidResult>(call.arguments)
                        notifyNasInvokeResult(ret, callback = listener.callback)
                    }
                    removeCallBackListener(method)
                    //通知flutter端调用成功
                    result?.success(null)
                } else {
                    result?.notImplemented()
                }
            }
        }
    }

    fun startConnect() {
        _logger.i("start to connect flutter")
        val bundle = buildInitBundle()
        _logger.i("send flutter data: $bundle")
        sink?.success(bundle.toString())  //等待sdk_init 回调
        notifyConnect()
    }

    private fun buildInitBundle(): NasBundle {
        return NasBundle(
            method = NasMethodConst.EVENT_METHOD_SDK_INIT,
            args = mapOf("appkey" to appkey, "appsecret" to appsecret)
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

    override fun invoke(bundle: NasBundle, callback: INasCallback?) {
        if (!bundle.isVerify()) {
            notifyNasInvokeResult(
                BasicEventResult.buildFailure(code = NasResultCode.CODE_BAD_REQUEST),
                callback
            )
            return
        }
        //构造callback监听器，异步在【handleMethodCall】中进行监听
        addCallbackListener(bundle.method, callback)
        //发起flutterCall
        sink?.success(bundle.toString())
    }

    private fun addCallbackListener(method: String, callback: INasCallback?) {
        if (_listeners.containsKey(method)) {
            _listeners.remove(method)
        }
        _listeners[method] = NasFlutterCallbackListener.newBuilder(callback)
    }

    private fun removeCallBackListener(method: String) {
        _listeners.remove(method)
    }

    private fun notifyNasInvokeResult(result: BasicEventResult<*>?, callback: INasCallback?) {
        if (callback == null || result == null) return
        if (result.success()) {
            callback.onSuccess(result.data)
        } else {
            callback.onError(result.code!!, result.message)
        }
    }

    private fun notifyNasResult(bundle: NasBundle) {
        connector?.onBundleReceived(bundle)
    }

    override fun broadEvent(event: NasEvent, args: Any?) {
    }

    override fun fireEvent(event: NasEvent, args: Any?, callback: INasCallback?) {
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
        TODO("Not yet implemented")
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