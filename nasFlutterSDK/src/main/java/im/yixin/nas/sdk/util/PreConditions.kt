package im.yixin.nas.sdk.util

import android.os.Looper
import android.text.TextUtils

/**
 * Created by jixia.cai on 2021/2/20 9:52 AM
 */
object PreConditions {

    fun <T> checkNotNull(target: T?, message: String?): T {
        requireNotNull(target) { message ?: "Must not be null!" }
        return target
    }

    fun checkArgument(condition: Boolean, message: String?): Boolean {
        require(condition) {
            message ?: "Condition must be true!"
        }
        return condition
    }

    fun checkNotEmpty(string: String?, message: String?): String? {
        require(!TextUtils.isEmpty(string)) { message ?: "Must not be null or empty!" }
        return string
    }


    fun <T : Collection<*>> checkNotEmpty(collection: T?, message: String?): T {
        require(!(collection == null || collection.isEmpty())) {
            message ?: "Collection must not be null or empty!"
        }
        return collection
    }

    fun checkMainThread(message: String?) {
        check(Looper.getMainLooper() == Looper.myLooper()) {
            message ?: "Current is not under main-thread!"
        }
    }

    fun checkWorkThread(message: String?) {
        check(Looper.getMainLooper() != Looper.myLooper()) {
            message ?: "Current is under main-thread!"
        }
    }
}