package com.sds.spp;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sds.mchat.MainApplication;

import javax.annotation.Nullable;

import static com.wix.reactnativenotifications.Defs.TOKEN_RECEIVED_EVENT_NAME;

public class SppManager {

    public static final String TAG = "SppManager";

    public static final String EXTRA_KEY_APP_ID = "appId";
    public static final String APP_ID = "dfb27236d365bad3";
    public static final String APP_SECRET = "5vxC9GAh2uBgk93J+XEgk/KofUs=";

    public static final String ACTION_START_SPP = "com.sec.spp.push.PUSH_CLIENT_SERVICE_ACTION";
    public static final String SPP_PACKAGE_NAME = "com.sec.spp.push";

    public static final String EXTRA_REQTYPE = "reqType";
    public static final int PUSH_REQ_TYPE_REGISTRATION = 1;
    public static final int PUSH_REQ_TYPE_DEREGISTRATION = 2;

    public static final String EXTRA_KEY_USERDATA = "userdata";

    public static final String SPP_MARKET_URI = "market://details?id=" + SPP_PACKAGE_NAME;

    public static void requestRegistration(Context context) {
        if (!checkInstallSPP(context)) {
            installSPP(context);
            return;
        }
        Intent i = new Intent(ACTION_START_SPP);
        i.setPackage(SPP_PACKAGE_NAME);
        i.putExtra(EXTRA_REQTYPE, PUSH_REQ_TYPE_REGISTRATION);
        i.putExtra(EXTRA_KEY_APP_ID, APP_ID);
        i.putExtra(EXTRA_KEY_USERDATA, context.getPackageName());
        context.startService(i);
    }

    public static void requestDeregistration(Context context) {
        if (!checkInstallSPP(context)) {
            return;
        }
        Intent i = new Intent(ACTION_START_SPP);
        i.setPackage(SPP_PACKAGE_NAME);
        i.putExtra(EXTRA_REQTYPE, PUSH_REQ_TYPE_DEREGISTRATION);
        i.putExtra(EXTRA_KEY_APP_ID, APP_ID);
        context.startService(i);
    }

    public static void registerRegResultReceiver(Context context) {
        if (!checkInstallSPP(context)) {
            return;
        }
        SppRegResultReceiver receiver = new SppRegResultReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SppRegResultReceiver.PUSH_REGISTRATION_CHANGED_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public static void deregisterRegResultReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    public static boolean checkInstallSPP(Context context) {
        PackageManager packageManager = context.getPackageManager();

        boolean isInstall;

        try {
            isInstall = packageManager.getApplicationInfo(SPP_PACKAGE_NAME, 0).enabled;

        } catch (PackageManager.NameNotFoundException e) {
            isInstall = false;
        }

        if (!isInstall) {
            Toast.makeText(context, "SPP is not installed.", Toast.LENGTH_SHORT).show();
        }

        return isInstall;
    }

    public static void installSPP(Context context) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(SPP_MARKET_URI));
            context.startActivity(i);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Google Play Store is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updatePushToken(@Nullable String appId, @Nullable String regId) {
        if (TextUtils.isEmpty(appId)) {
            appId = "";
        }
        if (TextUtils.isEmpty(regId)) {
            regId = "";
        }

        ReactContext reactContext = MainApplication.getReactContext();

        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter =
                    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

            eventEmitter.emit(TOKEN_RECEIVED_EVENT_NAME, regId);
        }
    }
}
