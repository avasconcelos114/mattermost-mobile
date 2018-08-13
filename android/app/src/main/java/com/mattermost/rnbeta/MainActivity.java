package com.mattermost.rnbeta;

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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mattermost.rnbeta.sso.RouteManager;
import com.mattermost.rnbeta.sso.SSOManager;
import com.reactnativenavigation.controllers.SplashActivity;
import com.sds.ems.utils.UpdateApplication;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class MainActivity extends SplashActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_READ_PHONE_STATE_PERMISSION = 100;

    private SSOManager mSsoManager = SSOManager.getInstance();
    private RouteManager mRouteManager = RouteManager.getInstance();

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
            bindRouteManager();

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

    private void bindRouteManager() {
        mRouteManager.bindService(this, new RouteManager.BindCallback() {
            @Override
            public void onServiceConnected(boolean isEnabled) {
                bindSsoManager();
            }

            @Override
            public void onError(@Nonnull String errorMsg) {
                ExitAlert("Bind Error", errorMsg);
            }
        });
    }

    private void bindSsoManager() {
        mSsoManager.bindService(this, new SSOManager.BindCallback() {
            @Override
            public void onServiceConnected(boolean isEnabled) {
                if (isEnabled) {
                    appVersionCheck();

                } else {
                    ExitAlert("Bas Error", "Please login BAS");
                }
            }

            @Override
            public void onError(@Nonnull String errorMsg) {
                ExitAlert("Bind Error", errorMsg);
            }
        });
    }

    private void appVersionCheck() {
        mSsoManager.versionChecker(this, new SSOManager.versionCheckCallback() {
            @Override
            public void onLatest(@Nonnull String responseCode) {
                getDataFromBAS();
            }

            @Override
            public void onUpdate(@Nonnull String responseCode, @Nonnull HashMap<String, String> userInfo) {
                UpdateApplication updateApplication =
                        UpdateApplication.getInstance(MainApplication.getContext(), userInfo);

                updateApplication.doUpdate();
            }

            @Override
            public void onError(@Nonnull String responseCode) {
                ExitAlert("SSO Error", "error code: " + responseCode);
            }
        });
    }

    private void getDataFromBAS() {
        mSsoManager.getDataFromBAS(mRouteManager, new SSOManager.BasCallback() {
            @Override
            public void onSuccess(@Nonnull Map<String, String> userInfo, @Nonnull Map<String, String> url) {
                sendBasInfo(userInfo, url);
            }

            @Override
            public void onBasLocked() {
                ExitAlert("BAS Error", "Please unLock BAS.");
            }

            @Override
            public void onSsoError() {
                ExitAlert("BAS Error", "isSingleSignOn: false.");
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
                    bindRouteManager();

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
        mSsoManager.unbindService(this);
        mRouteManager.unbindService(this);
    }

}
