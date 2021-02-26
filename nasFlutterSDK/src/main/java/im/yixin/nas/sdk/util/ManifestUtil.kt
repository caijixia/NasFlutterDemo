package im.yixin.nas.sdk.util

import android.content.Context
import android.content.pm.PackageManager

/**
 * Created by jixia.cai on 2021/2/19 1:48 PM
 */
class ManifestUtil {

    companion object {
        fun readMetaInfo(context: Context, key: String): String? {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val value = appInfo.metaData.get(key)
            if (value is String?) {
                return value
            } else if (value is Number) {
                return value.toString();
            }
            return null
        }
    }
}