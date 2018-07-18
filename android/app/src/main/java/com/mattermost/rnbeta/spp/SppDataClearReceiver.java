package com.mattermost.rnbeta.spp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Data of Samsung Push Service could be deleted by user.  In that case, registration information of apps is deleted also and they no more get push message from server.
 * So, whenever receiving data clear event of Samsung Push Service, then try registration again.
 */
public class SppDataClearReceiver extends BroadcastReceiver {

    public static final String TAG = "SppDataClearReceiver";
    public static final String SPP_PKG_NAME = "com.sec.spp.push";
    public static final String INTENT_ACTION = "android.intent.action.PACKAGE_REMOVED";

    @Override
    public void onReceive(Context context, Intent intent) {

        Uri uri = intent.getData();
        String action = intent.getAction();

        if (uri == null) {
            return;
        }

        if (!TextUtils.equals(action, INTENT_ACTION)) {
            return;
        }

        String pkgName = uri.getSchemeSpecificPart();

        if (!SPP_PKG_NAME.equals(pkgName)) {
            return;
        }

        boolean dataRemoved = intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false);

        if (!dataRemoved) {
            return;
        }

        SppManager.requestDeregistration(context);
        Log.i(TAG, "SPP's data removed. Need to try registration again");
    }
}
