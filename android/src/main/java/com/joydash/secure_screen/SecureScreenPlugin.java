package com.joydash.secure_screen;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceView;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

/** SecureScreenPlugin */
public class SecureScreenPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "secure_screen");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
      activity = activityPluginBinding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("secureScreen")) {
           boolean secure = call.argument("secure");
            boolean status = setSecureSurfaceView(secure);
            result.success(status);
          }else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private final boolean setSecureSurfaceView(boolean secure) {
      ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
      if (!isNonEmptyContainer((View) content)) {
          return false;
      } else {
          View splashView = content.getChildAt(0);
          //Intrinsics.checkExpressionValueIsNotNull(splashView, "splashView");
          if (!isNonEmptyContainer(splashView)) {
              return false;
          } else {
              View flutterView = ((ViewGroup) splashView).getChildAt(0);
              //Intrinsics.checkExpressionValueIsNotNull(flutterView,          "flutterView");
              if (!isNonEmptyContainer(flutterView)) {
                  return false;
              } else {
                  View surfaceView = ((ViewGroup) flutterView).getChildAt(0);
                  if (!(surfaceView instanceof SurfaceView)) {
                      return false;
                  } else {
                      ((SurfaceView) surfaceView).setSecure(secure);

                      if(secure)
                        activity.getWindow().setFlags(8192, 8192);
                      else
                        activity.getWindow().clearFlags(8192);

                      return true;
                  }
              }
          }
      }
  }

  private final boolean isNonEmptyContainer(View view) {
      if (!(view instanceof ViewGroup)) {
          return false;
      } else {
          return ((ViewGroup) view).getChildCount() >= 1;
      }
  }
}
