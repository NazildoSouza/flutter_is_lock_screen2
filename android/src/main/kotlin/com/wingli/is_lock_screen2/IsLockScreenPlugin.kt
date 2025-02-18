package com.wingli.is_lock_screen2

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.PowerManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.MethodChannel.Result

/** IsLockScreenPlugin */
public class IsLockScreenPlugin : FlutterPlugin, MethodCallHandler {

  private var bindingContext: Context? = null
  private var methodChannel: MethodChannel? = null


  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    this.bindingContext = binding.applicationContext
    methodChannel = MethodChannel(binding.binaryMessenger, "is_lock_screen")
    methodChannel!!.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
    bindingContext = null
    methodChannel!!.setMethodCallHandler(null)
    methodChannel = null

  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "isLockScreen" -> {
        val context = bindingContext
        ?: return result.error("NullContext", "Cannot access system service as context is null", null)

        val keyguardManager: KeyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val inKeyguardRestrictedInputMode: Boolean = keyguardManager.inKeyguardRestrictedInputMode()

        val isLocked = if (inKeyguardRestrictedInputMode) {
          true
        } else {
          val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            !powerManager.isInteractive
          } else {
            !powerManager.isScreenOn
          }
        }
        return result.success(isLocked)
      }
      else -> {
        return result.notImplemented()
      }
    }
  }

}
