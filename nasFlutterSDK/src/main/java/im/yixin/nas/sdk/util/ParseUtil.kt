package im.yixin.nas.sdk.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import im.yixin.nas.sdk.event.base.VoidResult
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/25 2:58 PM
 */
object ParseUtil {

    var sGson: Gson = GsonBuilder().create()

    inline fun <reified T> parseObject(arguments: Any?): T? {
        try {
            if (arguments == null) return null
            val type = object: TypeToken<T>(){}.type
            if (arguments is Map<*, *>) {
                return sGson.fromJson(sGson.toJson(arguments), type)
            } else if (arguments is JSONObject) {
                return sGson.fromJson(arguments.toString(), type)
            } else if (arguments is String) {
                return sGson.fromJson(arguments, type)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return null
    }

    fun parseVoidResult(arguments: Any?): VoidResult? {
        try {
            if (arguments == null) return null
            if (arguments is Map<*, *>) {
                return sGson.fromJson(sGson.toJson(arguments), VoidResult::class.java)
            } else if (arguments is JSONObject) {
                return sGson.fromJson(arguments.toString(), VoidResult::class.java)
            } else if (arguments is String) {
                return sGson.fromJson(arguments, VoidResult::class.java)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return null
    }
}