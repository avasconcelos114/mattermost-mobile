package com.sds.semp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sds.mobile.servicebrokerLib.ServiceBrokerLib;
import com.sds.mobile.servicebrokerLib.event.ResponseEvent;
import com.sds.mobile.servicebrokerLib.event.ResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SempManager {

    public static final String TAG = SempManager.class.getSimpleName();

    public static final String DS_Y = "DS_Y";
    public static final String DS_N = "DS_N";

    public static final String ACTIVE_FG_Y = "Y";
    public static final String ACTIVE_FG_N = "N";

    public static final String INOUT_TYPE_IN = "IN";
    public static final String INOUT_TYPE_OUT = "OUT";

    private static SempManager INSTANCE = null;

    private OkHttpClient mOkHttpClient;

    private ServiceBrokerLib mServiceBrokerLib;

    private TelephonyManager mTelephonyManager;

    private OnSempCallback mOnSempCallback;

    private SempManager(Context context) {
        mServiceBrokerLib = new ServiceBrokerLib(new ResponseListener() {
            @Override
            public void receive(ResponseEvent responseEvent) {
                int resultCode = responseEvent.getResultCode();
                String resultData = responseEvent.getResultData();

                switch (resultCode) {
                    case SempResultCode.SUCCESS:
                        try {
                            JSONArray jsonArray = new JSONArray(resultData);
                            JSONObject data = jsonArray.getJSONObject(0);

                            mOnSempCallback.onSuccess(
                                    resultCode,
                                    data.getString("ACTIVE_FG"),
                                    data.getString("INOUTTYPE")

                            );

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mOnSempCallback.onFailure(SempResultCode.JSON_PARSE_ERROR, "json parse error.");
                        }
                        break;

                    default:
                        mOnSempCallback.onFailure(resultCode, resultData);
                        break;
                }
                mOnSempCallback = null;

                Log.d(TAG, "responseEvent.code = " + responseEvent.getResultCode());
                Log.d(TAG, "responseEvent.data = " + responseEvent.getResultData());
            }
        });
        mOkHttpClient = new OkHttpClient();
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static SempManager getInstance(Context context) {
        synchronized (SempManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new SempManager(context);
            }
        }
        return INSTANCE;
    }

    public void checkDS(@Nonnull final Activity activity, @Nonnull String id, @Nonnull final OnDsCheckedCallback callback) {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(SempConstant.SCHEME)
                .host(SempConstant.HOST)
                .addPathSegments(SempConstant.PATH)
                .addQueryParameter(SempConstant.QUERY_KNOX_ID, id)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ResponseBody responseBody = response.body();
                                    if (responseBody != null) {
                                        String data = responseBody.string();
                                        Log.d(TAG, data);
                                        callback.onSuccess(call, data);
                                    }

                                } catch (IOException e) {
                                    callback.onFailure(call, e);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(call, e);
                            }
                        });
                        e.printStackTrace();
                    }
                });
    }

    public void request(String userId, String ipAddress, OnSempCallback callback) {
        try {
            String parameter = SempKey.PHONE_NO +
                    URLEncoder.encode(getPhoneNumber(), "UTF-8");

            Intent requestData = new Intent();

            requestData.putExtra(SempKey.USER_ID, userId);
            requestData.putExtra(SempKey.IP_ADDRESS, ipAddress);
            requestData.putExtra(SempKey.PARAMETER, parameter);

            requestData.putExtra(SempKey.S_CODE, SempConstant.S_CODE);
            requestData.putExtra(SempKey.S_TYPE, SempConstant.S_TYPE);
            requestData.putExtra(SempKey.DATA_TYPE, SempConstant.DATA_TYPE);
            requestData.putExtra(SempKey.E_KEY_TYPE, SempConstant.E_KEY_TYPE);
            requestData.putExtra(SempKey.CONTEXT_URL, SempConstant.CONTEXT_URL);
            requestData.putExtra(SempKey.PORT_NUMBER, SempConstant.PORT_NUMBER);
            requestData.putExtra(SempKey.CONNECTION_TYPE, SempConstant.CONNECTION_TYPE);
            requestData.putExtra(SempKey.PARAM_ENCRYPTED, SempConstant.PARAM_ENCRYPTED);
            requestData.putExtra(SempKey.PARAM_COMPRESSED, SempConstant.PARAM_COMPRESSED);
            requestData.putExtra(SempKey.USER_ADVANCE_KEY, SempConstant.USER_ADVANCE_KEY);

            Log.d(TAG, "SEMP requestData = " + requestData.getExtras());

            mOnSempCallback = callback;
            mServiceBrokerLib.request(requestData);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public String getPhoneNumber() {
        String phoneNumber = mTelephonyManager.getLine1Number();
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10, phoneNumber.length());
            phoneNumber = "0" + phoneNumber;
        }
        Log.i(TAG, "phoneNumber = " + phoneNumber);
        return phoneNumber;
    }

    public interface OnDsCheckedCallback {
        void onSuccess(@NonNull Call call, @NonNull final String body);

        void onFailure(@NonNull Call call, @NonNull IOException e);
    }

    public interface OnSempCallback {
        void onSuccess(int code, String activeFG, String inOutType);

        void onFailure(int code, String errorMessage);

    }
}
