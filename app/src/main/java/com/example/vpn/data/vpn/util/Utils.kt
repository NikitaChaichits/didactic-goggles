package com.example.vpn.data.vpn.util

import android.net.Uri
import com.example.vpn.R

object Utils {
    /**
     * Convert drawable image resource to string
     *
     * @param resourceId drawable image resource
     * @return image path
     */
    fun getImgURL(resourceId: Int): String {

        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse(
            "android.resource://" + R::class.java.getPackage().name + "/" + resourceId
        ).toString()
    }
}