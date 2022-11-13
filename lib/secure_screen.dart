import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class SecureScreen {
  static bool? _bScreenRecording = false, _bCheckForScreenRecording = true;
  static Timer? timer;
  static Function? onScreenRecordingStatus;

  static const MethodChannel _channel = const MethodChannel('secure_screen');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool?> secureScreen(bool secure,
      {Function? onScreenRecordingStatus}) async {
    bool? bSecured = false;

    if (Platform.isAndroid) {
      bSecured =
          await (_channel.invokeMethod('secureScreen', {"secure": secure}));
      return bSecured;
    } else if (Platform.isIOS) {
      if (secure) {
        _bCheckForScreenRecording = true;

        if (timer == null)
          timer = Timer.periodic(
              Duration(seconds: 2), (Timer t) => checkForScreenRecording());
      } else {
        _bCheckForScreenRecording = false;
        timer?.cancel();
        timer = null;
      }

      return _bScreenRecording;
    }

    return bSecured;
  }

  static checkForScreenRecording() async {
    print('Checking screen recording');
    _bScreenRecording = await _channel.invokeMethod('checkScreenRecording');
  }
}
