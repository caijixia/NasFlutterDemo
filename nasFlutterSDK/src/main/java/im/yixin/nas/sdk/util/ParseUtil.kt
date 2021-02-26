package im.yixin.nas.sdk.util

import com.google.gson.GsonBuilder
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/25 2:58 PM
 */
object ParseUtil {

    var mGson = GsonBuilder().create()

    inline fun <reified T> parseEventResult(arguments: Any?): T? {
        if (arguments == null) return null
        if (arguments is Map<*, *>) {
            return mGson.fromJson(mGson.toJson(arguments), T::class.java)
        } else if (arguments is JSONObject) {
            return mGson.fromJson(arguments.toString(), T::class.java)
        }
        return null
    }
}