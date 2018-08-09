package com.mattermost.rnbeta;

import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mattermost.rnbeta.sso.SSORequestKey;

import java.util.Map;

public class MattermostManagedModule extends ReactContextBaseJavaModule {

    private static MattermostManagedModule instance;

    private boolean shouldBlurAppScreen = false;

    private MattermostManagedModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    public static MattermostManagedModule getInstance(ReactApplicationContext reactContext) {
        if (instance == null) {
            instance = new MattermostManagedModule(reactContext);
        }

        return instance;
    }

    public static MattermostManagedModule getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "MattermostManaged";
    }

    public void setBasInfo(Map<String, String> userInfo, Map<String, String> url) {
        final WritableMap args = Arguments.createMap();

        String epId = userInfo.get(SSORequestKey.EPID);
        String userId = userInfo.get(SSORequestKey.USERID);
        String ssoUrl = url.get(SSORequestKey.SSO);
        String baseUrl = url.get(SSORequestKey.BASE);

        boolean isReady =
                (userId != null && epId != null && baseUrl != null && ssoUrl != null);

        args.putString("epId", epId);
        args.putString("userId", userId);
        args.putString("ssoUrl", ssoUrl);
        args.putString("baseUrl", baseUrl);
        args.putBoolean("isReady", isReady);

        sendBasInfo(args);
    }

    public void sendBasInfo(WritableMap args) {
        DeviceEventManagerModule.RCTDeviceEventEmitter emitter = MainApplication.getReactContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

        emitter.emit("managedInfoFromBAS", args);
    }

    @ReactMethod
    public void blurAppScreen(boolean enabled) {
        shouldBlurAppScreen = enabled;
    }

    public boolean isBlurAppScreenEnabled() {
        return shouldBlurAppScreen;
    }

    @ReactMethod
    public void getConfig(final Promise promise) {
        try {
            Bundle config = NotificationsLifecycleFacade.getInstance().getManagedConfig();

            if (config != null) {
                Object result = Arguments.fromBundle(config);
                promise.resolve(result);
            } else {
                throw new Exception("The MDM vendor has not sent any Managed configuration");
            }
        } catch (Exception e) {
            promise.reject("no managed configuration", e);
        }
    }
}
