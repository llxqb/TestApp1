package com.jeanboy.cropview.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import android.os.Message
import android.widget.Toast
import java.util.*

/**
 *  Created by li.liu  on 2021/4/8
 */


/*
 * 手电筒（开启闪光灯）工具类
 * 使用前，先申请闪光灯权限 <uses-permission android:name="android.permission.FLASHLIGHT" />
 * 部分手机需要摄像头权限： <uses-permission android:name="android.permission.CAMERA" /> 注：注意android5.0以上权限调用
 * 使用前，请先用hasFlashlight()方法判断设备是否有闪光灯
 * 务必在activity或fragment的onDestroy()方法里调用lightsOff()方法，确保Camera被释放
 *
 * 使用方法
 *  开启sos:new FlashlightUtils().sos()
 *  开启闪光灯: new FlashlightUtils().lightsOn()
 */
class FlashlightUtils {
    companion object {
        init {
            try {
                Class.forName("android.hardware.Camera")
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            }
        }
    }

    private var mCamera: Camera? = null
    private var manager: CameraManager? = null
    private var isSos = false
    fun isOff(): Boolean {
        return if (isVersionM()) {
            manager == null
        } else mCamera == null
    }

    //打开手电筒
    fun lightsOn(context: Context) {
        lightsOn(context, false)
    }

    //关闭手电筒
    fun lightOff() {
        lightsOff(false)
    }

    private fun lightsOn(context: Context, isSos: Boolean) {
        if (!isSos) offSos()
        if (hasFlashlight(context)) {
            if (isVersionM()) {
                linghtOn23(context)
            } else {
                lightOn22()
            }
        } else {
            Toast.makeText(
                context,
                "您的手机不支持开启闪光灯",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * 安卓6.0以上打开手电筒
     */
    @TargetApi(Build.VERSION_CODES.M)
    private fun linghtOn23(context: Context) {
        try {
            manager =
                context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            manager!!.setTorchMode("0", true) // "0"是主闪光灯
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * android6.0以下打开手电筒
     */
    private fun lightOn22() {
        if (mCamera == null) {
            mCamera = Camera.open()
            val params = mCamera?.getParameters()
            params?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            mCamera?.setParameters(params)
        }
    }

    private var mTimerTask: TimerTask? = null
    private var mTimer: Timer? = null
    private var mInt = 0
    private var context: Context? = null

    //关闭sos
    fun offSos() {
        isSos = false
        if (mTimer == null) return
        mTimer!!.cancel()
        mTimer = null
    }

    fun isSos(): Boolean {
        return isSos
    }

    /**
     * 打开sos
     *
     * @param context
     * @param speed   闪烁速度，建议取值1~6
     */
    fun sos(context: Context, speed: Int) {
        offSos()
        if (speed <= 0) {
            throw RuntimeException("speed不能小于等于0")
        }
        this.context = context
        isSos = true
        mTimerTask = object : TimerTask() {
            override fun run() {
                val message = Message()
                mInt = if (mInt == 0) 1 else 0
                message.what = mInt
                handler.sendMessage(message)
            }
        }
        mTimer = Timer()
        mTimer!!.schedule(mTimerTask, 0, 1500 / speed.toLong())
    }

    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> lightsOn(context!!, true)
                0 -> lightsOff(true)
            }
        }
    }

    private fun lightsOff(isSos: Boolean) {
        if (!isSos) offSos()
        if (isVersionM()) {
            lightsOff23()
        } else {
            lightsOff22()
        }
    }

    //安卓6.0以下关闭手电筒
    private fun lightsOff22() {
        if (mCamera != null) {
            val params = mCamera!!.parameters
            params.flashMode = Camera.Parameters.FLASH_MODE_OFF
            mCamera!!.parameters = params
            mCamera!!.release()
            mCamera = null
        }
    }

    //安卓6.0以上打关闭电筒
    @TargetApi(Build.VERSION_CODES.M)
    private fun lightsOff23() {
        try {
            if (manager == null) {
                return
            }
            manager!!.setTorchMode("0", false)
            manager = null
        } catch (e: Exception) {
        }
    }

    private fun isVersionM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * 判断设备是否有闪光灯
     *
     * @param context
     * @return true 有 false 没有
     */
    fun hasFlashlight(context: Context): Boolean {
        return context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
}