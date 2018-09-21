package com.sds.spp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.architectgroup.mchat.spp.PushMsgReceiver;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.notification.PushNotification;

public class ExtendPushMsgReceiver extends PushMsgReceiver {

    @Override
    public void onMessageReceived(@NonNull Context context, @NonNull Bundle bundle) {
        try {
            IPushNotification notification = PushNotification.get(context.getApplicationContext(), bundle);
            notification.onReceived();

        } catch (IPushNotification.InvalidNotificationException e) {
            e.printStackTrace();
        }
    }

}
