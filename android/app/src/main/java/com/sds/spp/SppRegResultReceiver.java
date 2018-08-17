package com.sds.spp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class SppRegResultReceiver extends BroadcastReceiver {

    public static final String TAG = "SppRegResultReceiver";

    public static final String PUSH_REGISTRATION_CHANGED_ACTION = "com.sec.spp.RegistrationChangedAction";

    public static final String EXTRA_REGID = "RegistrationID";
    public static final String EXTRA_ERROR = "Error";
    public static final String EXTRA_PUSH_STATUS = "com.sec.spp.Status";

    public static final int PUSH_REGISTRATION_SUCCESS = 0;
    public static final int PUSH_REGISTRATION_FAIL = 1;
    public static final int PUSH_DEREGISTRATION_SUCCESS = 2;
    public static final int PUSH_DEREGISTRATION_FAIL = 3;

    @Override
    public void onReceive(Context context, Intent intent) {

        String appId = intent.getStringExtra("appId");

        if (TextUtils.isEmpty(appId) || !appId.equals(SppManager.APP_ID)) {
            Log.d(TAG, "This isn't my result. appID : " + appId);
            return;
        }

        int result = intent.getIntExtra(EXTRA_PUSH_STATUS, PUSH_REGISTRATION_FAIL);

        switch (result) {
            case PUSH_REGISTRATION_SUCCESS:
                // 서버에 regId를 저장하는 작업 필요
                String regId = intent.getStringExtra(EXTRA_REGID);
                SppManager.updatePushToken(appId, regId);

                Log.d(TAG, "onReceive: PUSH_REGISTRATION_SUCCESS");
                Log.d(TAG, "regid = " + regId);
                break;


            case PUSH_REGISTRATION_FAIL:
                // id 등록 실패
                int errorCode = intent.getIntExtra(EXTRA_ERROR, SppStatus.UNDEFINED_ERROR);
                Log.d(TAG, "onReceive: PUSH_DEREGISTRATION_SUCCESS, errorCode: " + errorCode);
                break;


            case PUSH_DEREGISTRATION_SUCCESS:
                // 서버에서 regId를 제거하는 작업 필요
                SppManager.updatePushToken(null, null);
                Log.d(TAG, "onReceive: PUSH_DEREGISTRATION_SUCCESS");
                break;


            case PUSH_DEREGISTRATION_FAIL:
                // id 제거 실패
                errorCode = intent.getIntExtra(EXTRA_ERROR, SppStatus.UNDEFINED_ERROR);
                Log.d(TAG, "onReceive: PUSH_DEREGISTRATION_FAIL, errorCode: " + errorCode);
                break;

            default:
                Log.d(TAG, "onReceive: undefined result code.");

        }
        context.unregisterReceiver(this);
    }
}
