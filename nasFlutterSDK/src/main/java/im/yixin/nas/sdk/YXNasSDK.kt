package im.yixin.nas.sdk

import android.app.Application
import android.content.Context
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import io.flutter.embedding.android.FlutterFragment
import org.jetbrains.annotations.NotNull

/**
 * Created by jixia.cai on 2021/2/19 1:32 PM
 */
class YXNasSDK {

    companion object {

        const val NAS_ATTR_APP_KEY: String = "nas_app_key"

        const val NAS_ATTR_APP_SECRET: String = "nas_app_secret"

        @UiThread
        fun init(
            @NotNull app: Application,
            @NotNull appKey: String,
            @NotNull appSecret: String,
            connector: INasInvokeConnector? = null
        ): YXNasSDK {
            TODO()
        }

        fun init(
            @NotNull app: Application,
            connector: INasInvokeConnector? = null
        ): YXNasSDK {
            return FlutterFragment.NewEngineFragmentBuilder().build()
        }

        fun verify(): Boolean {
            return true
        }

        private fun readFromManifest(context: Context) {

        }
    }

    private constructor()

    @UiThread
    fun obtainFlutterHost(): Fragment {
        TODO()
    }
}

interface INasInvokeConnector {

    fun onBundleReceived(bundle: NasBundle)

    fun onBridgeConnected(bridge: INasChannelBridge)

    fun onBridgeDisconnected()
}

interface INasChannelBridge {

    fun invoke(bundle: NasBundle, callback: INasCallback? = null)

    fun broadEvent(event: NasEvent, args: Any?)

    fun fireEvent(event: NasEvent, args: Any?, callback: INasCallback? = null)
}

enum class NasEvent {
}

data class NasBundle(val method: String, val args: Any? = null)

object NasMethodConst {

}

interface INasCallback {

    fun onSuccess(result: Any? = null)

    fun onError(code: Int, message: String?)

    //maybe only use onMessage for callback
    fun onMessage(code: Int, message: String?, data: Any?)
}