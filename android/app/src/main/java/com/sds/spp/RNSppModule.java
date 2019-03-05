package com.sds.spp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.architectgroup.mchat.spp.SppManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sds.mchatdev.MainApplication;

public class RNSppModule extends ReactContextBaseJavaModule {

    public static final String TAG = "RNSppModule";

    private final SppManager mSppManager = SppManager.getInstance();

    public RNSppModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SppModuleAndroid";
    }

    @ReactMethod
    public void requestSppRegId() {
        Log.d(TAG, "requestSppRegId!!");
        Context applicationContext = MainApplication.getContext();
        mSppManager.register(applicationContext, new SppManager.SppInstalledCallback() {
            @Override
            public void onUnInstalled(@NonNull String message) {
                Log.d(TAG, message);
            }
        });
    }

}
