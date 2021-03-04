package im.yixin.nas.sdk.fragment

import im.yixin.nas.sdk.core.NasFlutterEngine
import im.yixin.nas.sdk.plugin.NasBridgePlugin
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.util.GeneratedPluginRegister

/**
 * Created by jixia.cai on 2021/2/20 10:16 AM
 */
class NasFlutterFragment : FlutterFragment() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        GeneratedPluginRegister.registerGeneratedPlugins(flutterEngine)
        if (flutterEngine !is NasFlutterEngine) {
            flutterEngine.plugins.add(NasBridgePlugin())
        }
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {
        super.cleanUpFlutterEngine(flutterEngine)
        if (flutterEngine !is NasFlutterEngine) {
            flutterEngine.plugins.remove(NasBridgePlugin::class.java)
        }
    }
}

class NasFlutterFragmentBuilder : FlutterFragment.CachedEngineFragmentBuilder {

    constructor(engineId: String) : super(NasFlutterFragment::class.java, engineId)
}