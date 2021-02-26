package im.yixin.nas.sdk.util

import android.content.Context
import android.content.pm.ApplicationInfo

/**
 * Created by jixia.cai on 2021/2/20 9:38 AM
 */
object AppUtil {

    @JvmStatic
    fun isAppDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}