package com.sds.mchat;

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

import com.architectgroup.mchat.bas.BasManager;
import com.architectgroup.mchat.bas.SSOManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.reactnativenavigation.controllers.SplashActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends SplashActivity {

    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 100;

    private BasManager mBasManager = BasManager.getInstance();

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

        loadGif();

        if (isPermissionsGranted()) {
            bindBasManager();

        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE_PERMISSION);
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

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.imgLogo);
        simpleDraweeView.setController(controller);
    }

    private boolean isPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= 23) {

            int isPermissionStatus = ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_PHONE_STATE);

            return isPermissionStatus == PackageManager.PERMISSION_GRANTED;

        } else {
            return true;
        }
    }

    private void bindBasManager() {
        mBasManager.bindService(this, new BasManager.OnBindCallback() {
            @Override
            public void onBound(boolean isEnabled) {
                if (isEnabled) {
                    checkAppVersion();
                } else {
                    ExitAlert("Error", "BAS is not login");
                }
            }

            @Override
            public void onError(@NonNull String errorMsg) {
                ExitAlert("Error", errorMsg);
            }
        });
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
                sendBasInfo(userInfo, urlInfo);
            }

            @Override
            public void onError(@NonNull String errorMsg) {
                ExitAlert("Error", errorMsg);
            }
        });
    }

    public void sendBasInfo(final Map<String, String> userInfo, final Map<String, String> url) {
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
        mBasManager.unBindService(this);
    }

}
