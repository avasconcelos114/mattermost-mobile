package com.sds.spp;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.sds.mchat.MainApplication;

public class RNSppModule extends ReactContextBaseJavaModule {

    public static final String TAG = "RNSppModule";

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
        SppManager.registerRegResultReceiver(applicationContext);
        SppManager.requestRegistration(applicationContext);
    }

}
