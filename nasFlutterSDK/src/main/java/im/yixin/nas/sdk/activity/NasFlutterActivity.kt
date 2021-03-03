package im.yixin.nas.sdk.activity

import im.yixin.nas.sdk.plugin.NasBridgePlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

/**
 * Created by jixia.cai on 2021/2/25 9:08 AM
 */
class NasFlutterActivity : FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        flutterEngine.plugins.add(NasBridgePlugin())
    }

    override fun cleanUpFlutterEngine(flutterEngine: FlutterEngine) {
        super.cleanUpFlutterEngine(flutterEngine)
        flutterEngine.plugins.remove(NasBridgePlugin::class.java)
    }
}