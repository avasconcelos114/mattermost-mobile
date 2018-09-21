package com.sds.spp;

import android.content.Context;
import android.support.annotation.Nullable;

import com.architectgroup.mchat.spp.SppDataClearReceiver;

public class ExtendSppDataClearReceiver extends SppDataClearReceiver {

    @Override
    public void onPreUnRegistration(@Nullable Context context) {
        super.onPreUnRegistration(context);
        // regId 제거 전 작업
    }

    @Override
    public void onPostUnRegistration(@Nullable Context context) {
        super.onPostUnRegistration(context);
        // regId 제거 후 작업
    }

}
