package com.huawei.gmsandhmsdemo;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.GoogleApiAvailability;
import com.huawei.hms.api.HuaweiApiAvailability;

public class HmsGmsUtil {
    private static final String TAG = "HmsGmsUtil";

    /**
     * Whether the HMS service on the device is available.
     *
     * @param context android context
     * @return true:HMS service is available; false:HMS service is not available;
     */
    public static boolean isHmsAvailable(Context context) {
        boolean isAvailable = false;
        if (null != context) {
            int result = HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context);
            isAvailable = (com.huawei.hms.api.ConnectionResult.SUCCESS == result);
        }
        Log.i(TAG, "isHmsAvailable: " + isAvailable);
        return isAvailable;
    }

    /**
     * Whether the GMS service on the device is available.
     *
     * @param context android context
     * @return true:GMS service is available; false:GMS service is not available;
     */
    public static boolean isGmsAvailable(Context context) {
        boolean isAvailable = false;
        if (null != context) {
            int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            isAvailable = (com.google.android.gms.common.ConnectionResult.SUCCESS == result);
        }
        Log.i(TAG, "isGmsAvailable: " + isAvailable);
        return isAvailable;
    }
}
