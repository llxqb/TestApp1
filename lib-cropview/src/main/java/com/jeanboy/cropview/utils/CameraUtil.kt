//package camerademo.utils
//
//import android.graphics.ImageFormat
//import android.hardware.Camera
//
///**
// *  Created by li.liu  on 2021/4/8
// */
//
//
///**
// * Desc: 相机工具类
// * author: RedRose
// * Date: 2019/3/20
// * Email: yinsxi@163.com
// */
//class CameraUtil private constructor() {
//    /**
//     * 相机参数对象
//     */
//    private var mParameters: Camera.Parameters? = null
//    private var mCamera: Camera? = null
//
//    @JvmOverloads
//    fun openCamera(id: Int = 0): Camera? {
//        if (mCamera == null) {
//            mCamera = Camera.open(id)
//        }
//        setProperty()
//        return mCamera
//    }
//
//    /**
//     * 相机属性设置
//     */
//    private fun setProperty() {
//        //设置相机预览页面旋转90°，（默认是横屏）
//        mCamera!!.setDisplayOrientation(90)
//        mParameters = mCamera!!.parameters
//        //设置将保存的图片旋转90°（竖着拍摄的时候）
//        mParameters?.setRotation(90)
//        mParameters?.setPreviewSize(1920, 1080)
//        mParameters?.setPictureSize(1920, 1080)
//        //        mParameters.setPictureSize(4608, 3456);
//        mParameters?.setPictureFormat(ImageFormat.JPEG)
//        mParameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
//        //        mParameters.set(ImageFormat.YUV_444_888);
//        mCamera!!.parameters = mParameters
//    }
//
//    /**
//     * 选装图片的角度
//     */
//    fun setRotateDegree(degree: Int) {
////        boolean result = degree == 0 || degree == 90
////        mParameters.setRotation(90);
//        if (mCamera != null) {
//            mParameters = mCamera!!.parameters
//            mParameters?.setRotation(degree)
//            mCamera!!.parameters = mParameters
//        }
//    }
//
//    /**
//     * 获取支持的预览分辨率
//     */
//    val previewSizeList: List<Camera.Size>
//        get() {
//            if (mCamera == null) {
//                throw NullPointerException("Camera can not be null")
//            }
//            return mCamera!!.parameters.supportedPreviewSizes
//        }
//
//    /**
//     * 获取保存图片支持的分辨率
//     */
//    val pictureSizeList: List<Camera.Size>
//        get() {
//            if (mCamera == null) {
//                throw NullPointerException("Camera can not be null")
//            }
//            return mCamera!!.parameters.supportedPictureSizes
//        }
//
//    /**
//     * 设置闪光灯模式
//     */
//    fun setFlashMode(mode: Int) {
//        mParameters = mCamera!!.parameters
//        val flashMode = mParameters.getFlashMode()
//        when (mode) {
//            FLASH_AUTO -> mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO)
//            FLASH_OFF -> mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
//            FLASH_ON -> mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON)
//            else -> {
//            }
//        }
//        mCamera!!.parameters = mParameters
//    }
//
//    /**
//     * 释放相机资源
//     */
//    fun releaseCamera() {
//        if (mCamera != null) {
//            mCamera!!.setPreviewCallback(null)
//            mCamera!!.stopPreview()
//            mCamera!!.lock()
//            mCamera!!.release()
//            mCamera = null
//            isRelease = true
//        }
//    }
//
//    /**
//     * 是否旋转图片 true 选装
//     */
//    var isRelease = false
//        private set
//
//    /**
//     * 设置保存图片的分辨率
//     */
//    fun setSaveSize(saveSize: Camera.Size) {
//        mParameters!!.setPictureSize(saveSize.width, saveSize.height)
//        mCamera!!.parameters = mParameters
//    }
//
//    companion object {
//        private var mInstance: CameraUtil? = null
//
//        /**
//         * 闪光灯自动
//         */
//        const val FLASH_AUTO = 0
//
//        /**
//         * 闪光灯关闭
//         */
//        const val FLASH_OFF = 1
//
//        /**
//         * 闪光灯开启
//         */
//        const val FLASH_ON = 2
//        private val o = Any()
//        val instance: CameraUtil?
//            get() {
//                if (mInstance == null) {
//                    synchronized(o) {
//                        if (mInstance == null) {
//                            mInstance = CameraUtil()
//                        }
//                    }
//                }
//                return mInstance
//            }
//    }
//}