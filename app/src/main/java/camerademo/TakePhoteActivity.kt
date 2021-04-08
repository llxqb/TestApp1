package camerademo

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.OrientationEventListener
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jeanboy.cropview.camare.CameraPreview
import com.jeanboy.cropview.cropper.CropActivity
import com.jeanboy.cropview.cropper.CropperHandler
import com.jeanboy.cropview.cropper.CropperParams
import com.s20cxq.testapp.R
import kotlinx.android.synthetic.main.activity_take_phote.*
import java.io.*

class TakePhoteActivity : AppCompatActivity(), CameraPreview.OnCameraStatusListener,
    SensorEventListener, CropperHandler {
    var orientation = 0
    private var mOrientationEventListener: OrientationEventListener? = null//监听屏幕方向

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_take_phote)
        orientation = resources.configuration.orientation
        Log.e("---", "onCreate()  orientation=$orientation")
        cameraPreview.setFocusView(view_focus)
        cameraPreview.setOnCameraStatusListener(this)
        mSensorManager =
            getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccel = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        initAnim()
        initListener()
    }


    var orientationType = 0// 0 正竖屏  1右横屏 2倒竖屏 3左横屏
    private fun initListener() {
        mOrientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation > 350 || orientation < 20) { //0度  90 正竖屏
//                    Log.e("---", "正竖屏")
                    if (orientationType == 1) {
//                        startAnim(90f,0f)//逆时针旋转90度
                        animator2?.start()
                    } else if (orientationType == 2) {
//                        startAnim(180f,0f)//逆时针旋转180度
//                        animator1?.start()
                    } else if (orientationType == 3) {
//                        startAnim(270f,0f)//顺时针旋转180度
                        animator3?.start()
                    }
                    orientationType = 0
                } else if (orientation in 71..109) { //90度 右横屏
//                    Log.e("---", "右横屏")

                    if (orientationType == 0) {
                        animator1?.start()
                    } else if (orientationType == 2) {
//                        startAnim(180f,0f)//逆时针旋转180度
//                        animator1?.start()
                    } else if (orientationType == 3) {
//                        startAnim(270f,0f)//顺时针旋转180度
//                        animator2?.start()
                    }
                    orientationType = 1
                } else if (orientation in 161..199) { //180度 倒竖屏
//                    Log.e("---", "倒竖屏")
//                    if(orientationType==0){
//                        animatorLand?.start();
//                    }
                    orientationType = 2
                } else if (orientation in 251..289) { //270度 左横屏
//                    Log.e("---", "左横屏")
                    if (orientationType == 0) {
                        animator?.start()
                    } else if (orientationType == 1) {
//                        startAnim(180f,0f)//逆时针旋转180度
//                        animator1?.start()
                    } else if (orientationType == 2) {
//                        startAnim(270f,0f)//顺时针旋转180度
//                        animator2?.start()
                    }
                    orientationType = 3
                }

                if (orientationType == 0 || orientationType == 2) {
                    referenceLine.setLand(true)
                } else {
                    referenceLine.setLand(false)
                }
            }
        }
        if (mOrientationEventListener!!.canDetectOrientation()) {
            mOrientationEventListener!!.enable() //开启
        } else {
            mOrientationEventListener!!.disable()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mOrientationEventListener?.disable() //注销
    }


    var animator: ObjectAnimator? = null
    var animator1: ObjectAnimator? = null
    var animator2: ObjectAnimator? = null
    var animator3: ObjectAnimator? = null
    private fun initAnim() {
        //顺时针旋转90度
        animator = ObjectAnimator.ofFloat(hint, "rotation", 0f, 90f)//, 0f, 90f
        animator?.startDelay = 800
        animator?.duration = 600
        animator?.interpolator = LinearInterpolator()
        //逆时针旋转90度
        animator1 = ObjectAnimator.ofFloat(hint, "rotation", 0f, -90f)//, 0f, 90f
        animator1?.startDelay = 800
        animator1?.duration = 600
        animator1?.interpolator = LinearInterpolator()
        //顺时针旋转90度
        animator2 = ObjectAnimator.ofFloat(hint, "rotation", -90f, 0f)//, 0f, 90f
        animator2?.startDelay = 800
        animator2?.duration = 600
        animator2?.interpolator = LinearInterpolator()
        //逆时针旋转90度
        animator3 = ObjectAnimator.ofFloat(hint, "rotation", 90f, 0f)//, 0f, 90f
        animator3?.startDelay = 800
        animator3?.duration = 600
        animator3?.interpolator = LinearInterpolator()
    }


    override fun onResume() {
        super.onResume()
        Log.e("---", "onResume（）")
//        ScreenRotateUtils.getInstance(this).start(this)
//        ScreenRotateUtils.getInstance(this).toggleRotate()
//        if (!isRotated) {
//            val view =
//                findViewById<View>(R.id.crop_hint)
//            val animSet = AnimatorSet()
//            val animator1 =
//                ObjectAnimator.ofFloat(view, "rotation", 0f, 90f)
//            val moveIn =
//                ObjectAnimator.ofFloat(view, "translationX", 0f, -50f)
//            animSet.play(animator1).before(moveIn)
//            animSet.duration = 10
//            animSet.start()
//            isRotated = true
//        }
        mSensorManager?.registerListener(
            this,
            mAccel,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun clickFlash(view: View?) {
        Log.e("----", "点亮")
//       FlashUtils.init(this)
//       FlashUtils.open()
//        val flashlight = FlashlightUtils()
//        flashlight.lightsOn(this)
    }

    //开始拍照
    fun takePhoto(view: View?) {
        if (cameraPreview != null) {
            cameraPreview.takePicture()
        }
    }

    fun close(view: View?) {
        finish()
    }


    /**
     * 拍照停止回调
     */
    override fun onCameraStopped(data: ByteArray) {
        val bitmap =
            BitmapFactory.decodeByteArray(data, 0, data.size)
        val filename = "cropper_123" + ".jpg"
        cameraCacheUri =
            Uri.fromFile(this.externalCacheDir).buildUpon().appendPath(filename)
                .build()
        insertImage(filename, bitmap, data)
        Log.e(TAG, "uri="+cameraCacheUri.toString())
//        notifyImageToGallery(this, cameraCacheUri)
//
//        val intent = Intent(this, CropActivity::class.java)
//            .putExtra(CropperParams.PICK_URI, cameraCacheUri)
//            .putExtra(CropperParams.ASPECT_X, params.aspectX)
//            .putExtra(CropperParams.ASPECT_Y, params.aspectY)
//        startActivityForResult(intent, CropperParams.REQUEST_CROPPED)
    }


    private var cameraCacheUri: Uri? = null
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        result: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, result)
//        Log.e(TAG, "resultCode=$resultCode")
//        if (resultCode == Activity.RESULT_CANCELED) {
//            onCropCancel()
//        } else if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                CropperParams.REQUEST_PICK_CAMERA -> {
////                    notifyImageToGallery(this, cameraCacheUri)
////                    startActivityForResult(
////                        Intent(this, CropActivity::class.java)
////                            .putExtra(CropperParams.PICK_URI, cameraCacheUri)
////                            .putExtra(CropperParams.ASPECT_X, params.aspectX)
////                            .putExtra(CropperParams.ASPECT_Y, params.aspectY),
////                        CropperParams.REQUEST_CROPPED
////                    )
//                }
//                CropperParams.REQUEST_PICK_IMAGE -> if (result != null) {
//                    startActivityForResult(
//                        Intent(this, CropActivity::class.java)
//                            .putExtra(CropperParams.PICK_URI, result.data)
//                            .putExtra(CropperParams.ASPECT_X, params.aspectX)
//                            .putExtra(CropperParams.ASPECT_Y, params.aspectY),
//                        CropperParams.REQUEST_CROPPED
//                    )
//                } else {
//                    onCropFailed(MSG_FILE_NOT_FOUND)
//                }
//                CropperParams.REQUEST_KITKAT_PICK_IMAGE -> if (result != null) {
//                    startActivityForResult(
//                        Intent(this, CropActivity::class.java)
//                            .putExtra(
//                                CropperParams.PICK_URI,
//                                com.jeanboy.cropview.util.Utils.ensureUriPermission(this, result)
//                            )
//                            .putExtra(CropperParams.ASPECT_X, params.aspectX)
//                            .putExtra(CropperParams.ASPECT_Y, params.aspectY),
//                        CropperParams.REQUEST_CROPPED
//                    )
//                } else {
//                    onCropFailed(MSG_FILE_NOT_FOUND)
//                }
//                CropperParams.REQUEST_CROPPED -> if (result != null) {
//                    val uri =
//                        result.extras!!.getParcelable<Uri>(CropperParams.PICK_URI)
//                    onCropped(uri!!)
//                } else {
//                    onCropFailed(MSG_ERROR)
//                }
//            }
//        }
//    }

    /**
     * 同步图片到系统图库
     *
     * @param context
     * @param uri
     */
    private fun notifyImageToGallery(context: Context, uri: Uri?) {
        //把文件插入到系统图库
        try {
            val file = File(uri!!.path)
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                file.name,
                null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 通知图库更新
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri
            )
        )
    }

    /**
     * 插入图片
     */
    private fun insertImage(
        filename: String,
        source: Bitmap?,
        jpegData: ByteArray?
    ): Uri? {
        var outputStream: OutputStream? = null
        val file: File
        try {
//            val matrix = Matrix()
            file = File(this.externalCacheDir, filename)
//            if (file.exists()) {
//                file.delete();
//            }

//            outputStream = FileOutputStream(file)
//            source?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            matrix.postRotate(90f)
//            outputStream.write(jpegData)
            if (file.createNewFile()) {
                outputStream = FileOutputStream(file)
                source?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    ?: outputStream.write(jpegData)
            }
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message!!)
            return null
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
            return null
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (t: Throwable) {
                }
            }
        }
        val uri =
            FileProvider.getUriForFile(this, "com.s20cxq.testapp" + ".fileprovider", file)
        return uri
    }


    private var mLastX = 0f
    private var mLastY = 0f
    private var mLastZ = 0f
    private var mInitialized = false
    private var mSensorManager: SensorManager? = null
    private var mAccel: Sensor? = null
    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        if (!mInitialized) {
            mLastX = x
            mLastY = y
            mLastZ = z
            mInitialized = true
        }
        val deltaX = Math.abs(mLastX - x)
        val deltaY = Math.abs(mLastY - y)
        val deltaZ = Math.abs(mLastZ - z)
        if (deltaX > 0.8 || deltaY > 0.8 || deltaZ > 0.8) {
            cameraPreview!!.setFocus()
        }
        mLastX = x
        mLastY = y
        mLastZ = z
    }

    override fun onAccuracyChanged(
        sensor: Sensor,
        accuracy: Int
    ) {
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getParams(): CropperParams {
        return CropperParams(1, 1)
    }

    override fun onCropped(uri: Uri) {
        try {
//            cropImageView.setImageBitmap(
//                MediaStore.Images.Media.getBitmap(
//                    this.contentResolver,
//                    uri
//                )
//            )
            //            mCropImageView.rotateImage(90);
        } catch (e: IOException) {
            Log.e(TAG, e.message!!)
        }
    }

    override fun onCropCancel() {}
    override fun onCropFailed(msg: String) {}

    companion object {
        private const val TAG = "TakePhoteActivity"
        const val MSG_FILE_NOT_FOUND = "图片不可用"
        const val MSG_ERROR = "操作失败"
    }
}