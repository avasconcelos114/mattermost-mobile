package com.mattermost.rnbeta.spp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;

public class PushMsgReceiver extends BroadcastReceiver {

    public static final String TAG = "PushMsgReceiver";

    /**
     * SPP push data payload key.
     */
    public static final class SppExtra {
        public static final String APP_ID = "appId";
        public static final String NOTIFICATION_ID = "notificationId";
        public static final String SENDER = "sender";
        public static final String ACK = "ack";
        public static final String MSG = "msg";
        public static final String APP_DATA = "appData";
        public static final String TIMESTAMP = "timeStamp";
        public static final String SESSION_INFO = "sessionInfo";
        public static final String CONNECTION_TERM = "connectionTerm";
    }


    /**
     * SPP data payload의 APP_DATA에 해당하는 json key.
     * App data payload key.
     */
    public static final class AppDataExtra {
        public static final String PLATFORM = "platform";
        public static final String POST_ID = "post_id";
        public static final String TEAM_ID = "team_id";
        public static final String ROOT_ID = "root_id";
        public static final String SERVER_ID = "server_id";
        public static final String DEVICE_ID = "device_id";
        public static final String SENDER_ID = "sender_id";
        public static final String CHANNEL_ID = "channel_id";
        public static final String CHANNEL_NAME = "channel_name";
        public static final String TYPE = "type";
        public static final String CATEGORY = "category";
        public static final String TITLE = "title";
        public static final String MESSAGE = "message";
        public static final String SUB_TEXT = "subText";
        public static final String BADGE = "badge";
        public static final String SOUND = "sound";
        public static final String CONT_AVA = "cont_ava";
        public static final String SMAILL_ICON = "smallIcon";
        public static final String LARGE_ICON = "largeIcon";
        public static final String USER_INFO = "largeIcon";
        public static final String FROM_WEBHOOK = "from_webhook";
        public static final String OVERRIDE_USERNAME = "override_username";
        public static final String OVERRIDE_ICON_URL = "override_icon_url";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");

        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            return;
        }

        Context applicationContext = context.getApplicationContext();
        Bundle mchatPayload = toMChatPayload(bundle);

        printPayload(mchatPayload);
//        Log.d(TAG, "mchat payload: " + mchatPayload.toString());

        try {
            IPushNotification notification = PushNotification.get(applicationContext, mchatPayload);
            notification.onReceived();

        } catch (IPushNotification.InvalidNotificationException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    private Bundle toMChatPayload(@Nonnull Bundle bundle) {
        Bundle mchatPayload = new Bundle();

        // Message.
        String message = bundle.getString(SppExtra.MSG, "");
        mchatPayload.putString(AppDataExtra.MESSAGE, message);

        // App Data(string json) to Bundle.
        if (bundle.containsKey(SppExtra.APP_DATA)) {
            String strAppData = bundle.getString(SppExtra.APP_DATA, "");

            Bundle appData = jsonToBundle(strAppData);
            mchatPayload.putAll(appData);
        }

        return mchatPayload;
    }

    @Nonnull
    private Bundle jsonToBundle(@Nonnull String strJson) {
        try {
            if (strJson.startsWith("\"")) {
                strJson = strJson.substring(1, strJson.length());
            }
            if (strJson.endsWith("\"")) {
                strJson = strJson.substring(0, strJson.length() - 1);
            }
            strJson = strJson.replace("\\", "");
            return jsonToBundle(new JSONObject(strJson));

        } catch (JSONException e) {
            return new Bundle();
        }
    }

    @NonNull
    private Bundle jsonToBundle(@Nonnull JSONObject jsonObject) {

        Bundle bundle = new Bundle();
        Iterator<String> iterator = jsonObject.keys();

        try {
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = jsonObject.get(key);

                if (value instanceof JSONObject) {
                    JSONObject innerObject = (JSONObject) value;
                    Bundle innerBundle = jsonToBundle(innerObject.toString());

                    bundle.putBundle(key, innerBundle);

                } else {
                    bundle.putString(key, value.toString());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }

    private void printPayload(@Nonnull Bundle bundle) {
        if (bundle.isEmpty()) {
            Log.w(TAG, "SppExtra payload is empty");
            return;
        }

        Set<String> keySet = bundle.keySet();

        StringBuilder payload = new StringBuilder();
        payload.append("payload: \n");

        for (String key : keySet) {

            Object value = bundle.get(key);

            if (value == null) {
                continue;
            }

            payload.append(key)
                    .append(" = ")
                    .append(value.toString())
                    .append("\n");
        }

        Log.d(TAG, payload.toString());
    }
}