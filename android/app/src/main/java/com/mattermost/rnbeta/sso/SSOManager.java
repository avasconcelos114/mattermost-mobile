package com.mattermost.rnbeta.sso;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mattermost.rnbeta.MainApplication;
import com.sds.BizAppLauncher.sso.ISSOService;
import com.sds.ems.network.ResponseListener;
import com.sds.ems.utils.UpdateApplication;
import com.sds.routeservice.LaunchTimeProfiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by seokchan.kwon on 18. 07. 18.
 */

public final class SSOManager {

    private static final String TAG = "SSOManager";

    private static final String PACKAGE_NAME = "packageName";

    private static final String RESPONSE_CODE_UPDATE = "C200";
    private static final String RESPONSE_CODE_LATEST = "C201";
    private static final String RESPONSE_CODE_SERVER_ERROR = "C202";
    private static final String RESPONSE_CODE_OTHER_ERROR = "C203";

    private static final int IS_UNLOCKED = 2;
    private static final int SSO_STATUS_NONE = 0;

    private static final String BIZ_APP_LAUNCHER_PACKAGE_ID = "com.sds.BizAppLauncher";
    private static final String BIZ_APP_LAUNCHER_BIND_ACTION = BIZ_APP_LAUNCHER_PACKAGE_ID + ".sso.ISSOServiceBind";

    private int lockState;

    private boolean isBound;
    private boolean ssoState;

    private String appId;
    private String launcherLog;

    private Map<String, String> userInfo;

    private ISSOService service;

    private BindCallback mBindCallback;

    private static SSOManager instance;

    private SSOManager() {
        this.isBound = false;
    }

    public synchronized static SSOManager getInstance() {
        if (instance == null) {
            instance = new SSOManager();
        }
        return instance;
    }

    public void bindService(@Nonnull Context context, @Nonnull BindCallback callback) {
        mBindCallback = callback;

        try {
            String packageName = context.getPackageName();

            Intent intent = new Intent(BIZ_APP_LAUNCHER_BIND_ACTION);
            intent.putExtra(PACKAGE_NAME, packageName);
            intent.setPackage(BIZ_APP_LAUNCHER_PACKAGE_ID);

            context.bindService(intent, conn, Context.BIND_AUTO_CREATE);

        } catch (Exception e) {
            mBindCallback.onError("Could not found com.sds.BizAppLauncher.");
        }
    }

    public void unbindService(@Nonnull Context context) {
        if (isBound) {
            context.unbindService(conn);
        }
        mBindCallback = null;
    }

    public final boolean isEnabled() {
        return userInfo != null;
    }

    @SuppressWarnings("unchecked")
    private void saveUserInfo() throws RemoteException {
        String packageName = MainApplication.getContext().getPackageName();

        List<String> keys = Arrays.asList(
                SSORequestKey.USERID,
                SSORequestKey.EPID);

        userInfo = service.getUserInfo(keys, packageName);

        if (userInfo != null) {
            Log.d(TAG, userInfo.toString());
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, final IBinder binder) {
            SSOManager.this.service = ISSOService.Stub.asInterface(binder);
            String packageName = MainApplication.getContext().getPackageName();

            try {
                saveUserInfo();

                appId = service.getAppId(packageName);
                launcherLog = service.getLauncherLog("7", appId);

                Log.d(TAG, "appId : " + appId);
                Log.d(TAG, "launcherLog : " + launcherLog);

            } catch (RemoteException e) {
                e.printStackTrace();

            } catch (SecurityException e) {
                Log.e(TAG, "Binder invocation to an incorrect interface");
                return;
            }

            isBound = true;

            if (mBindCallback != null) {
                mBindCallback.onServiceConnected(isEnabled());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            isBound = false;
        }
    };

    public final int checkLockTime() {
        try {
            lockState = isBound ? service.checkLockTime() : SSO_STATUS_NONE;
            Log.d(TAG, "lockState : " + lockState);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return lockState;
    }

    public final boolean isSingleSignOn() {
        try {
            ssoState = isBound && service.isSingleSignOn();
            Log.d(TAG, "ssoState : " + ssoState);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ssoState;
    }

    public void versionChecker(@Nonnull Context context, @Nonnull final versionCheckCallback callback) {
        ResponseListener responseListener = new ResponseListener() {
            @Override
            public void receive(Object responseData) {
                String responseCode = responseData.toString();

                switch (responseCode) {
                    case RESPONSE_CODE_UPDATE:
                        callback.onUpdate(responseCode, (HashMap<String, String>) userInfo);
                        break;

                    case RESPONSE_CODE_LATEST:
                        callback.onLatest(responseCode);
                        break;

                    case RESPONSE_CODE_SERVER_ERROR:
                        callback.onError(responseCode);
                        break;

                    case RESPONSE_CODE_OTHER_ERROR:
                        callback.onError(responseCode);
                        break;
                }

            }
        };
        UpdateApplication.getInstance(context, (HashMap) userInfo)
                .checkUpdate(appId, launcherLog, responseListener, true);
    }

    public void getDataFromBAS(@Nonnull RouteManager routeManager, @Nonnull BasCallback callback) {
        if (checkLockTime() != IS_UNLOCKED) {
            callback.onBasLocked();

        } else if (!isSingleSignOn()) {
            callback.onSsoError();

        } else {
            Map<String, String> url = routeManager.getUrl();

            String host = url.get(SSORequestKey.BASE);
            String userId = userInfo.get(SSORequestKey.USERID);

            LaunchTimeProfiler profiler = LaunchTimeProfiler.getInstance();

            profiler.setUserId(userId);
            profiler.setRoute(host);

            routeManager.setUserInfo(userInfo);

            callback.onSuccess(userInfo, url);
        }
    }

    public interface BindCallback {
        void onServiceConnected(boolean isEnabled);

        void onError(@Nonnull String errorMsg);
    }

    public interface versionCheckCallback {

        void onLatest(@Nonnull String responseCode);

        void onUpdate(@Nonnull String responseCode, @Nonnull HashMap<String, String> userInfo);

        void onError(@Nonnull String responseCode);
    }

    public interface BasCallback {

        void onSuccess(@Nonnull Map<String, String> userInfo, @Nonnull Map<String, String> url);

        void onBasLocked();

        void onSsoError();
    }

}
