#import "SecureScreenPlugin.h"

@implementation SecureScreenPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"secure_screen"
            binaryMessenger:[registrar messenger]];
  SecureScreenPlugin* instance = [[SecureScreenPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"checkScreenRecording" isEqualToString:call.method]) {
        BOOL isRecording = [isScreenRecording];
        result(@(isRecording));
      } else {
    result(FlutterMethodNotImplemented);
  }
}

//TODO : Add check screen recording method for iOS
- (BOOL)isScreenRecording {
    for (UIScreen *screen in UIScreen.screens) {
        if ([screen respondsToSelector:@selector(isCaptured)]) {
            // iOS 11+ has isCaptured method.
            if ([screen performSelector:@selector(isCaptured)]) {
                return YES; // screen capture is active
            } else if (screen.mirroredScreen) {
                return YES; // mirroring is active
            }
        } else {
            // iOS version below 11.0
            if (screen.mirroredScreen)
                return YES;
        }
    }
    return NO;
}

@end
