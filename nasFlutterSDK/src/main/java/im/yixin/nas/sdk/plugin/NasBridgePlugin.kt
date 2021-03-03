package im.yixin.nas.sdk.plugin

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.core.NasFlutterBridge
import im.yixin.nas.sdk.core.NasFlutterBridgeStore
import im.yixin.nas.sdk.event.ConnectEvent
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.NasResponse
import im.yixin.nas.sdk.util.LogUtil
import im.yixin.nas.sdk.util.ParseUtil
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

/**
 * Created by jixia.cai on 2021/2/19 1:49 PM
 */
class NasBridgePlugin : FlutterPlugin, ActivityAware {

    companion object {

        const val EVENT_CHANNEL_NAME = "im.yixin.nas.sdk/nas-flutter-receiver"

        const val METHOD_CHANNEL_NAME = "im.yixin.nas.sdk/nas-flutter-invoker"
    }

    private var _logger = LogUtil.getLogger(YXNasConstants.compose("bridge"))

    private var _bridge: NasFlutterBridge? = null

    private fun ensureBridge(): NasFlutterBridge {
        if (_bridge == null) {
            _bridge = NasFlutterBridgeStore.instance.produce()
        }
        return _bridge!!
    }

    private fun parseResponse(any: Any?): NasResponse? {
        return ParseUtil.parseObject<NasResponse>(any)
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        _logger.i("Attach to flutter-engine ~")
        ensureBridge()
        EventChannel(binding.binaryMessenger, EVENT_CHANNEL_NAME).also {
            it.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    _logger.i("listen from flutter with arguments: $arguments")
                    //判断connect-event
                    val response = parseResponse(arguments)
                    if (response != null) {
                        if (response.method == YXNasConstants.Method.EVENT_METHOD_INNER_CONNECT) {
                            val connectResponse =
                                BaseNasResponse.parse(response = response) as ConnectEvent.Response?
                            //打印日志
                            if (connectResponse?.success == true) {
                                _bridge?.setup(events)
                                _bridge?.startConnect()
                            }
                        }
                    }
                }

                override fun onCancel(arguments: Any?) {
                    _logger.i("cancel listen from flutter with args: $arguments")
                }

            })
        }
        MethodChannel(binding.binaryMessenger, METHOD_CHANNEL_NAME).also {
            it.setMethodCallHandler { call, result ->
                _bridge?.handleMethodCall(call, result)
            }
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        _logger.i("Detach from flutter-engine ~")
        _bridge?.notifyDisconnect()
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