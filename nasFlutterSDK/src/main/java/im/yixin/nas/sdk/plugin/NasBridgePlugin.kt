package im.yixin.nas.sdk.plugin

import com.google.gson.Gson
import im.yixin.nas.sdk.NasBundle
import im.yixin.nas.sdk.NasMethodConst
import im.yixin.nas.sdk.core.NasFlutterBridge
import im.yixin.nas.sdk.core.NasFlutterBridgeStore
import im.yixin.nas.sdk.util.LogUtil
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/19 1:49 PM
 */
class NasBridgePlugin : FlutterPlugin, ActivityAware {

    companion object {

        const val EVENT_CHANNEL_NAME = "im.yixin.nas.sdk/nas-flutter-receiver"

        const val METHOD_CHANNEL_NAME = "im.yixin.nas.sdk/nas-flutter-invoker"
    }

    private var _logger = LogUtil.getLogger(NasBridgePlugin::class.java.simpleName)

    private var _bridge: NasFlutterBridge? = null

    private fun ensureBridge(): NasFlutterBridge {
        if (_bridge == null) {
            _bridge = NasFlutterBridgeStore.instance.produce()
        }
        return _bridge!!
    }

    private fun convert2Bundle(arguments: Any?): NasBundle? {
        if (arguments is JSONObject) {
            return Gson().fromJson(arguments.toString(), NasBundle::class.java)
        } else if (arguments is Map<*, *>) {
            return Gson().fromJson(JSONObject(arguments).toString(), NasBundle::class.java)
        }
        return null
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        _logger.i("Attach to flutter-engine ~")
        ensureBridge()
        EventChannel(binding.binaryMessenger, EVENT_CHANNEL_NAME).also {
            it.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    _logger.i("listen from flutter with args: $arguments")
                    ensureBridge().setup(events)
                    val bundle = convert2Bundle(arguments)
                    if (bundle?.isVerify() == false) return
                    if (bundle!!.method == NasMethodConst.EVENT_NAME_BRIDGE_CONNECT) {
                        ensureBridge().startConnect()
                    }
                }

                override fun onCancel(arguments: Any?) {
                    _logger.i("cancel listen from flutter with args: $arguments")
                }

            })
        }
        MethodChannel(binding.binaryMessenger, METHOD_CHANNEL_NAME).also {
            it.setMethodCallHandler { call, result ->
                _logger.i("receive flutter call: ${call.toJson()}")
                ensureBridge().handleMethodCall(call, result)
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        _logger.i("Detach from flutter-engine ~")
        ensureBridge().notifyDisconnect()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
    }

}

fun Any?.toJson(): String? {
    return try {
        Gson().toJson(this)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}