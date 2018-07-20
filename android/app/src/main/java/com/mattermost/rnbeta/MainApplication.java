package com.mattermost.rnbeta;

import com.facebook.react.bridge.ReactContext;
import com.mattermost.rnbeta.spp.RNSppPackage;
import com.mattermost.share.SharePackage;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.content.Context;
import android.os.Bundle;

import com.reactnativedocumentpicker.ReactNativeDocumentPicker;
import com.oblador.keychain.KeychainPackage;
import com.reactlibrary.RNReactNativeDocViewerPackage;
import com.brentvatne.react.ReactVideoPackage;
import com.horcrux.svg.SvgPackage;
import com.inprogress.reactnativeyoutube.ReactNativeYouTube;
import io.sentry.RNSentryPackage;
import com.masteratul.exceptionhandler.ReactNativeExceptionHandlerPackage;
import com.RNFetchBlob.RNFetchBlobPackage;
import com.gantix.JailMonkey.JailMonkeyPackage;
import io.tradle.react.LocalAuthPackage;
import com.github.godness84.RNRecyclerViewList.RNRecyclerviewListPackage;

import com.facebook.react.ReactPackage;
import com.facebook.soloader.SoLoader;
import com.facebook.drawee.backends.pipeline.Fresco;

import com.imagepicker.ImagePickerPackage;
import com.gnet.bottomsheet.RNBottomSheetPackage;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.psykar.cookiemanager.CookieManagerPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.BV.LinearGradient.LinearGradientPackage;
import com.reactnativenavigation.NavigationApplication;
import com.wix.reactnativenotifications.RNNotificationsPackage;
import com.wix.reactnativenotifications.core.notification.INotificationsApplication;
import com.wix.reactnativenotifications.core.notification.IPushNotification;
import com.wix.reactnativenotifications.core.AppLaunchHelper;
import com.wix.reactnativenotifications.core.AppLifecycleFacade;
import com.wix.reactnativenotifications.core.JsIOHelper;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

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
            new RNSppPackage()
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
  }

  @Override
  public boolean clearHostOnActivityDestroy(Activity activity) {
    // This solves the issue where the splash screen does not go away
    // after the app is killed by the OS cause of memory or a long time in the background
    return false;
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
