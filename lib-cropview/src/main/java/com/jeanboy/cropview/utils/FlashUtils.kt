package camerademo.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build

/**
 *  Created by li.liu  on 2021/4/8
 */
object FlashUtils {
    private lateinit var manager: CameraManager
    private lateinit var mCamera: Camera
    private lateinit var mContext: Context
    private var status = false //记录手电筒状态
    fun init(context: Context) {
        mContext = context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        }
    }

    //打开手电筒
    fun open() {
        if (status) { //如果已经是打开状态，不需要打开
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val packageManager = mContext.packageManager
            val features = packageManager.systemAvailableFeatures
            for (featureInfo in features) {
                if (PackageManager.FEATURE_CAMERA_FLASH == featureInfo.name) { // 判断设备是否支持闪光灯
//                    val parameters = mCamera.parameters
//                    parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
//                    mCamera.parameters = parameters
//                    mCamera.startPreview()
                }
            }
        }
        status = true //记录手电筒状态为打开
    }

    //关闭手电筒
    fun close() {
        if (!status) { //如果已经是关闭状态，不需要打开
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                manager.setTorchMode("0", false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
//            mCamera.stopPreview()
//            mCamera.release()
        }
        status = false //记录手电筒状态为关闭
    }
}
