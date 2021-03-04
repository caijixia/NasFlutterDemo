package im.yixin.nas.sdk.core

import android.content.Context
import im.yixin.nas.sdk.plugin.NasBridgePlugin
import im.yixin.nas.sdk.util.RandomUtil
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.plugins.util.GeneratedPluginRegister
import org.jetbrains.annotations.NotNull

/**
 * Created by jixia.cai on 2021/2/20 10:02 AM
 */
class NasFlutterEngineProxy {

    companion object {

        private val NAS_FLUTTER_ENGINE_ID =
            NasFlutterEngineProxy::class.java.canonicalName + "_" + RandomUtil.nextString()

        const val NAS_FLUTTER_INIT_ROUTE_DEFAULT = "/"

        const val NAS_FLUTTER_ENTRY_POINT_DEFAULT = "main"
    }

    private val _engineCache: FlutterEngineCache by lazy { FlutterEngineCache.getInstance() }

    private var flutterEngine: FlutterEngine? = null

    private var isRecycled = false

    fun preWarm(
        @NotNull context: Context,
        dartVmArgs: Array<String>? = null,
        automaticallyRegisterPlugins: Boolean? = true
    ) {
        flutterEngine = NasFlutterEngine(context, dartVmArgs, automaticallyRegisterPlugins ?: true)
        flutterEngine!!.navigationChannel.setInitialRoute(NAS_FLUTTER_INIT_ROUTE_DEFAULT)
        //预加载插件
        preloadPlugins(flutterEngine!!)
        flutterEngine!!.dartExecutor.executeDartEntrypoint(
            createEntryPoint(
                NAS_FLUTTER_ENTRY_POINT_DEFAULT
            )
        )
        FlutterEngineCache.getInstance().put(engineId, flutterEngine)
    }

    private fun preloadPlugins(engine: FlutterEngine) {
        GeneratedPluginRegister.registerGeneratedPlugins(engine)
        engine.plugins.add(NasBridgePlugin())
    }

    @Deprecated("")
    fun recycle() {
        if (!isRecycled) {
            flutterEngine?.destroy()
            _engineCache.remove(NAS_FLUTTER_ENGINE_ID)
            isRecycled = true
        }
    }

    private fun createEntryPoint(entry: String): DartExecutor.DartEntrypoint {
        return DartExecutor.DartEntrypoint(
            FlutterInjector.instance().flutterLoader().findAppBundlePath(), entry
        )
    }

    val engineId: String
        get() = NAS_FLUTTER_ENGINE_ID
}

class NasFlutterEngine(
    context: Context,
    dartVmArgs: Array<out String>?,
    automaticallyRegisterPlugins: Boolean?
) : FlutterEngine(context, dartVmArgs, automaticallyRegisterPlugins ?: true)