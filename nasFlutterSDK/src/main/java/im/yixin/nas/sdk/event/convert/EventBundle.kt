package im.yixin.nas.sdk.event.convert

import com.google.gson.Gson
import im.yixin.nas.sdk.event.callback.BasicEventResult

/**
 * Created by jixia.cai on 2021/2/25 2:43 PM
 */
open class EventBundle {

    var method: String? = null

    var result: BasicEventResult<*>? = null

    constructor(method: String, result: BasicEventResult<*>?) {
        this.method = method
        this.result = result
    }

    fun success(): Boolean = result?.success() ?: false

    override fun toString(): String {
        return Gson().toJson(this)
    }
}