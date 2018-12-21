package com.sds.mchat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.BV.LinearGradient.LinearGradientPackage;
import com.RNFetchBlob.RNFetchBlobPackage;
import com.architectgroup.mchat.bas.BasManager;
import com.architectgroup.mchat.bas.Configuration;
import com.architectgroup.mchat.spp.SppManager;
import com.architectgroup.mchat.spp.SppRegResultReceiver;
import com.brentvatne.react.ReactVideoPackage;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.soloader.SoLoader;
import com.gantix.JailMonkey.JailMonkeyPackage;
import com.github.godness84.RNRecyclerViewList.RNRecyclerviewListPackage;
import com.gnet.bottomsheet.RNBottomSheetPackage;
import com.horcrux.svg.SvgPackage;
import com.imagepicker.ImagePickerPackage;
import com.inprogress.reactnativeyoutube.ReactNativeYouTube;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.masteratul.exceptionhandler.ReactNativeExceptionHandlerPackage;
import com.oblador.keychain.KeychainPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.psykar.cookiemanager.CookieManagerPackage;
import com.reactlibrary.RNReactNativeDocViewerPackage;
import com.reactnativedocumentpicker.ReactNativeDocumentPicker;
import com.reactnativenavigation.NavigationApplication;
import com.sds.share.SharePackage;
import com.sds.spp.RNSppPackage;
import com.sds.spp.SppConstant;
import com.wix.reactnativenotifications.RNNotificationsPackage;
import com.wix.reactnativenotifications.core.AppLaunchHelper;
import com.wix.reactnativenotifications.core.AppLifecycleFacade;
import com.wix.reactnativenotifications.core.JsIOHelper;
import com.wix.reactnativenotifications.core.notification.INotificationsApplication;
import com.wix.reactnativenotifications.core.notification.IPushNotification;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import io.sentry.RNSentryPackage;
import io.tradle.react.LocalAuthPackage;
import com.github.godness84.RNRecyclerViewList.RNRecyclerviewListPackage;
import com.reactnativecommunity.webview.RNCWebViewPackage;
import com.swmansion.gesturehandler.react.RNGestureHandlerPackage;

import static com.wix.reactnativenotifications.Defs.TOKEN_RECEIVED_EVENT_NAME;

public class MainApplication extends NavigationApplication implements INotificationsApplication {
    public NotificationsLifecycleFacade notificationsLifecycleFacade;
    public Boolean sharedExtensionIsOpened = false;
    public Boolean replyFromPushNotification = false;

    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    @NonNull
    @Override
    public List<ReactPackage> createAdditionalReactPackages() {
        // Add the packages you require here.
        // No need to add RnnPackage and MainReactPackage
        return Arrays.<ReactPackage>asList(
                new ImagePickerPackage(),
                new RNBottomSheetPackage(),
                new RNDeviceInfo(),
                new CookieManagerPackage(),
                new VectorIconsPackage(),
                new SvgPackage(),
                new LinearGradientPackage(),
                new RNNotificationsPackage(this),
                new LocalAuthPackage(),
                new JailMonkeyPackage(),
                new RNFetchBlobPackage(),
                new MattermostPackage(this),
                new RNSentryPackage(),
                new ReactNativeExceptionHandlerPackage(),
                new ReactNativeYouTube(),
                new ReactVideoPackage(),
                new RNReactNativeDocViewerPackage(),
                new ReactNativeDocumentPicker(),
                new SharePackage(this),
                new KeychainPackage(),
                new InitializationPackage(this),
                new RNSppPackage(),
                new RNRecyclerviewListPackage(),
                new RNCWebViewPackage(),
                new RNGestureHandlerPackage()
        );
    }

    @Override
    public String getJSMainModuleName() {
        return "index";
    }

    public static Context getContext() {
        return instance;
    }

    public static ReactContext getReactContext() {
        return instance.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
    }

    public static boolean isInitReactNative() {
        return instance.isReactContextInitialized();
    }

    public static void waitInitReactContext(@Nonnull final Activity activity, @Nonnull final ReactContextInitCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                /**
                 * reactContext가 초기화 될 때까지 대기.
                 * reactContext 초기화 콜백 메소드가 따로 있는지 몰라서 임시로 작업
                 */
                while (true) {
                    if (MainApplication.isInitReactNative()) {
                        break;
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onInit();
                    }
                });

            }
        }).start();
    }

    public interface ReactContextInitCallback {
        void onInit();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Create an object of the custom facade impl
        notificationsLifecycleFacade = NotificationsLifecycleFacade.getInstance();
        // Attach it to react-native-navigation
        setActivityCallbacks(notificationsLifecycleFacade);

        SoLoader.init(this, /* native exopackage */ false);

        Fresco.initialize(this);

        initBas();
        initSpp();
    }

    private void initBas() {
        BasManager.init(new Configuration() {
            @NonNull
            @Override
            public String getScheme() {
                return MChatConstants.getDefaultScheme();
            }

            @NonNull
            @Override
            public String getHostName() {
                return MChatConstants.getDefaultHost();
            }

            @NonNull
            @Override
            public String getPort() {
                return MChatConstants.getDefaultPort();
            }

            @NonNull
            @Override
            public String getPackageName() {
                return MainApplication.this.getPackageName();
            }

            @Override
            public boolean isProd() {
                return MChatConstants.isProd();
            }
        });
    }

    private void initSpp() {
        SppManager.init(new com.architectgroup.mchat.spp.Configuration() {
            @NonNull
            @Override
            public String getAppId() {
                return SppConstant.APP_ID;
            }

            @NonNull
            @Override
            public String getSecretId() {
                return SppConstant.SECRET_ID;
            }

            @NonNull
            @Override
            public SppRegResultReceiver newResultReceiver() {
                return new SppRegResultReceiver();
            }

            @Override
            public void onUpdatePushToken(@NonNull String appId, @NonNull String regId) {
                ReactContext reactContext = MainApplication.getReactContext();

                if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
                    DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter =
                            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

                    eventEmitter.emit(TOKEN_RECEIVED_EVENT_NAME, regId);
                }
            }
        });
    }

    @Override
    public boolean clearHostOnActivityDestroy(Activity activity) {
        return super.clearHostOnActivityDestroy(activity);
    }

    @Override
    public IPushNotification getPushNotification(Context context, Bundle bundle, AppLifecycleFacade defaultFacade, AppLaunchHelper defaultAppLaunchHelper) {
        return new CustomPushNotification(
                context,
                bundle,
                notificationsLifecycleFacade, // Instead of defaultFacade!!!
                defaultAppLaunchHelper,
                new JsIOHelper()
        );
    }
}
