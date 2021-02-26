package im.yixin.nas.sdk.api

import android.content.Intent
import androidx.fragment.app.Fragment

/**
 * Created by jixia.cai on 2021/2/22 9:53 AM
 */
interface IYXNasApi {

    fun obtainFlutterHost(): Fragment

    fun obtainFlutterIntent(): Intent
}