package com.sds.sso;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.sds.mchat.MChatConstants;
import com.sds.routeservice.RouteService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by seokchan.kwon on 18. 07. 18.
 */

public class RouteManager {

    private static final String TAG = "RouteManager";

    private static final int MSG_GET_ROUTE = 1;

    private static final String BASE_URL = "base";
    private static final String SSO_URL = "sso";

    private static final String IP_ADDRESS = "ipAddress";
    private static final String PORT_NUMBER = "portnumber";
    private static final String CONNECTIONS_TYPE = "connectionType";

    private static final String ROUTER_SERVICE_PACKAGE_ID = "com.sds.routeservice";
    private static final String ROUTER_SERVICE_ACTION = ROUTER_SERVICE_PACKAGE_ID + ".ROUTE";

    private static final String BIZ_APP_LAUNCHER_PACKAGE_ID = "com.sds.BizAppLauncher";
    private static final String BIZ_APP_LAUNCHER_ACTION = BIZ_APP_LAUNCHER_PACKAGE_ID + ".ROUTE";

    private boolean mIsBound;
    private boolean isMessageReceived;

    private String ipAddress;
    private String portNumber;
    private String connectionType;
    private String userId;

    private Messenger mMessenger = new Messenger(new IncomingHandler());

    private BindCallback mBindCallback;

    private static RouteManager instance;

    private RouteManager() {
        mIsBound = false;
        isMessageReceived = false;
    }

    public synchronized static RouteManager getInstance() {
        if (instance == null) {
            instance = new RouteManager();
        }
        return instance;
    }

    public final Map<String, String> getUrl() {
        Map<String, String> url = new HashMap<String, String>();

        url.put(BASE_URL, getBaseUrl());
        url.put(SSO_URL, getSsoUrl());

        return url;
    }

    private String getBaseUrl() {
        return connectionType + "://" + ipAddress + ":" + portNumber;
    }

    private String getSsoUrl() {
        return connectionType + "://" + ipAddress + "/kms/jsp/saml/mattermost/mobile.jsp";
    }

    public final boolean isEnabled() {
        return mIsBound && isMessageReceived;
    }

    public void setUserInfo(Map<String, String> userInfo) {
        userId = userInfo.get(SSORequestKey.USERID);
    }

    public void bindService(@Nonnull Context context, @Nonnull BindCallback callback) {
        mBindCallback = callback;

        Intent intent = new Intent(BIZ_APP_LAUNCHER_ACTION);
        intent.setPackage(BIZ_APP_LAUNCHER_PACKAGE_ID);

        try {
            mIsBound = context.bindService(intent, conn, Service.BIND_AUTO_CREATE);

            if (!mIsBound) {
                intent = new Intent(ROUTER_SERVICE_ACTION);
                intent.setPackage(context.getPackageName());
                intent.putExtra(RouteService.EXTRA_USER_ID, userId);

                mIsBound = context.bindService(intent, conn, Service.BIND_AUTO_CREATE);

                if (!mIsBound) {
                    mBindCallback.onError("Could not found com.sds.BizAppLauncher");
                }
            }

        } catch (SecurityException e) {
            mIsBound = false;
            mBindCallback.onError("Failed to bind service by security issue");

        } catch (ActivityNotFoundException e) {
            mIsBound = false;
            mBindCallback.onError("BAS is not installed Please Install the BAS.");
        }
    }

    public void unbindService(@Nonnull Context context) {
        if (mIsBound) {
            context.unbindService(conn);
        }
        mBindCallback = null;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Messenger mService = new Messenger(service);

            try {
                Message msg = Message.obtain(null, MSG_GET_ROUTE);
                msg.replyTo = mMessenger;
                mService.send(msg);

            } catch (RemoteException e) {
                Log.e("onServiceConnected", e.getMessage());
            }

            if (mBindCallback != null) {
                mBindCallback.onServiceConnected(isEnabled());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    @SuppressLint("HandlerLeak")
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d("handleMessage", "Received a message");

            switch (msg.what) {

                case MSG_GET_ROUTE:
                    isMessageReceived = true;

                    ipAddress = msg.peekData().getString(IP_ADDRESS);
                    portNumber = msg.peekData().getString(PORT_NUMBER);
                    connectionType = msg.peekData().getString(CONNECTIONS_TYPE);

                    if (ipAddress == null) {
                        ipAddress = MChatConstants.getDefaultHost();
                    }

                    if (portNumber == null) {
                        portNumber = MChatConstants.getDefaultPort();
                    }

                    if (connectionType == null) {
                        connectionType = MChatConstants.getDefaultConnectionType();
                    }
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public interface BindCallback {
        void onServiceConnected(boolean isEnabled);

        void onError(@Nonnull String errorMsg);
    }

}


