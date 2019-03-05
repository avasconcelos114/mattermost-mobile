package com.sds.mchatdev;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.architectgroup.mchat.bas.BasManager;
import com.architectgroup.mchat.bas.SSOManager;
import com.architectgroup.mchat.bas.SSORequestKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.reactnativenavigation.controllers.SplashActivity;
import com.sds.semp.SempManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class MainActivity extends SplashActivity {

    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 100;

    private BasManager mBasManager;

    private SempManager mSempManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Reference: https://stackoverflow.com/questions/7944338/resume-last-activity-when-launcher-icon-is-clicked
         * 1. Open app from launcher/appDrawer
         * 2. Go home
         * 3. Send notification and open
         * 4. It creates a new Activity and Destroys the old
         * 5. Causing an unnecessary app restart
         * 6. This solution short-circuits the restart
         */
        if (!isTaskRoot()) {
            finish();
            return;
        }

        mBasManager = BasManager.getInstance();
        mSempManager = SempManager.getInstance(this);

        loadGif();

        if (isPermissionsGranted()) {
            bindBasManager();
        } else {
            requestPermissions();
        }
    }

    @Override
    public int getSplashLayout() {
        return R.layout.launch_screen;
    }

    private void loadGif() {
        Uri uri = Uri.parse(
                "res:///" + R.drawable.mosaic_loading_page);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        SimpleDraweeView simpleDraweeView = findViewById(R.id.imgLogo);
        simpleDraweeView.setController(controller);
    }

    private boolean isPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            int isReadSmsPermission = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_SMS);

            int isReadPhoneStatePermission = ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_PHONE_STATE);

            int isReadPhoneNumberPermission;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                isReadPhoneNumberPermission = ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_PHONE_NUMBERS);
            } else {
                isReadPhoneNumberPermission = PackageManager.PERMISSION_GRANTED;
            }

            return (isReadSmsPermission == PackageManager.PERMISSION_GRANTED) &&
                    (isReadPhoneStatePermission == PackageManager.PERMISSION_GRANTED) &&
                    (isReadPhoneNumberPermission == PackageManager.PERMISSION_GRANTED);

        } else {
            return true;
        }
    }

    private void requestPermissions() {
        String[] requestPermissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions = new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_NUMBERS
            };
        } else {
            requestPermissions = new String[]{
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_PHONE_STATE
            };
        }
        ActivityCompat.requestPermissions(
                this, requestPermissions, REQUEST_READ_PHONE_STATE_PERMISSION);
    }

    private void bindBasManager() {
        try {
            mBasManager.bindService(this, new BasManager.OnBindCallback() {
                @Override
                public void onBound(boolean isEnabled) {
                    if (isEnabled) {
                        checkAppVersion();
                    } else {
                        ExitAlert("Error", "BAS is not logged in");
                    }
                }

                @Override
                public void onError(@NonNull String errorMsg) {
                    ExitAlert("Error", errorMsg);
                }
            });

        } catch (NullPointerException e) {
            ExitAlert("Error", "BAS is not logged in");
        }
    }

    private void checkAppVersion() {
        mBasManager.checkAppVersion(this, new SSOManager.OnVersionCheckCallback() {
            @Override
            public void onLatest(@NonNull String responseCode) {
                getBasInfo();
            }

            @Override
            public void onNeedUpdate(@NonNull String responseCode, @NonNull HashMap<String, String> userInfo) {
                mBasManager.updateApplication(MainActivity.this);
            }

            @Override
            public void onError(@NonNull String errorMsg) {
                ExitAlert("Error", errorMsg);
            }
        });
    }

    private void getBasInfo() {
        mBasManager.getBasInfo(new BasManager.OnBasInfoCallback() {
            @Override
            public void onSuccess(@NonNull Map<String, String> userInfo, @NonNull Map<String, String> urlInfo) {
                checkDS(userInfo, urlInfo);
            }

            @Override
            public void onError(@NonNull String errorMsg) {
                ExitAlert("Error", errorMsg);
            }
        });
    }

    /**
     * DS 체크
     * DS_Y인 경우 SMEP 라이브러리를 사용하여, 추가 인증 단계 진행 후 JS로 BAS 정보 전달
     * DS_N인 경우 바로 JS로 BAS 정보 전달
     * <p>
     * "https://www.samsungsmartoffice.net/kms/jsp/wagle/mobile/common/semp/MobileMosaicSempDsCheck.jsp?knoxId={ID}"
     */
    private void checkDS(final Map<String, String> userInfo, final Map<String, String> urlInfo) {
        mSempManager.checkDS(this, userInfo.get(SSORequestKey.USERID), new SempManager.OnDsCheckedCallback() {
            @Override
            public void onSuccess(@NonNull Call call, @NonNull String body) {
                switch (body) {
                    case SempManager.DS_Y:
                        checkSemp(userInfo, urlInfo);
                        break;

                    case SempManager.DS_N:
                        sendBasInfoFromJs(userInfo, urlInfo);
                        break;

                    default:
                        ExitAlert("DS Error", "Need API verification.");
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ExitAlert("DS Error", "Need API verification.");
            }
        });
    }

    /**
     * SMEP 체크
     * "ACTIVE_FG": "Y" or "N"
     * "INOUTTYPE": "IN" or "OUT"
     * ACTIVE_FG가 Y && INOUTTYPE가 IN인 경우에만 로그인 진행
     * 그 외
     */
    private void checkSemp(final Map<String, String> userInfo, final Map<String, String> urlInfo) {
        String userId =
                userInfo.get(SSORequestKey.USERID);

        String ipAddress =
                Uri.parse(urlInfo.get(SSORequestKey.BASE)).getHost();

        mSempManager.request(userId, ipAddress, new SempManager.OnSempCallback() {
            @Override
            public void onSuccess(int code, String activeFG, String inOutType) {
                boolean isActiveFG = TextUtils.equals(activeFG, SempManager.ACTIVE_FG_Y);
                boolean isInOutType = TextUtils.equals(inOutType, SempManager.INOUT_TYPE_IN);

                if (isActiveFG && isInOutType) {
                    sendBasInfoFromJs(userInfo, urlInfo);

                } else {
                    ExitAlert(
                            "SEMP Error",
                            "In accordance with the security policy of the DS division,\nYou can not use the MOSAIC App outside the company.\n\nContact : ci.office@samsung.com"
                    );
                }
            }

            @Override
            public void onFailure(int code, String errorMessage) {
                ExitAlert("SEMP Error", errorMessage);
            }
        });
    }

    /**
     * REACT-NATIVE로 BAS 정보를 전달 (SSO 로그인에 사용)
     */
    public void sendBasInfoFromJs(final Map<String, String> userInfo, final Map<String, String> url) {
        MainApplication.waitInitReactContext(this, new MainApplication.ReactContextInitCallback() {
            @Override
            public void onInit() {
                MattermostManagedModule mattermostManagedModule = MattermostManagedModule.getInstance();
                mattermostManagedModule.setBasInfo(userInfo, url);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindBasManager();

                } else {
                    ExitAlert("Permission Rejected", "Please grant permission.");
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void ExitAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                appExit();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    private void appExit() {
        ActivityCompat.finishAffinity(this);
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBasManager != null) {
            mBasManager.unBindService(this);
        }
    }

}