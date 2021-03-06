package com.s20cxq.testapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.jeanboy.cropview.cropper.CropperHandler
import com.jeanboy.cropview.cropper.CropperManager
import com.jeanboy.cropview.cropper.CropperParams
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() , CropperHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
        )
       checkPermission(permissions)

        CropperManager.getInstance().build(this)


    }

    fun fromCamera(v: View?) {
//        MainActivityPermissionsDispatcher.pickFromCameraWithCheck(this)
        pickFromCamera()
    }

    fun fromGallery(v: View?) {
//        MainActivityPermissionsDispatcher.pickFromGalleryWithCheck(this)
        pickFromGallery()
    }

    fun toTestPage(v: View?){
        startActivity(Intent(this,TestActivity::class.java));
    }


    override fun getActivity(): Activity? {
        return this
    }

    override fun getParams(): CropperParams? {
        return CropperParams(1, 1)
    }


    fun checkPermission(permissions: List<String>){
        PermissionX.init(this)
                .permissions(permissions)
                .explainReasonBeforeRequest()
//            .onExplainRequestReason { scope, deniedList ->
//                val message = "app?????????????????????????????????????????????"
//                scope.showRequestReasonDialog(deniedList, message, "??????", "??????")
//            }
                .onForwardToSettings { scope, deniedList ->
//                    val message = "?????????????????????????????????????????????"
//                    val dialog = CustomDialog(this, message, deniedList)
//                    scope.showForwardToSettingsDialog(dialog)
                }
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
//                    Toast.makeText(activity, "?????????????????????????????????", Toast.LENGTH_SHORT).show()
//                        permissionListener?.permissionSuccess()

                    } else {
                        Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show()
//                        permissionListener?.permissionFail()
                    }
                }
    }

    override fun onCropped(uri: Uri) {
        Log.d("=====onCropped======", "======????????????=======$uri")
        try {
            val bm = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            iv_cropped.setImageBitmap(bm)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCropCancel() {
        Log.d("=====onCropCancel====", "======????????????=====")
    }

    override fun onCropFailed(msg: String) {
        Log.d("=====onCropFailed===", "=======????????????======$msg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CropperManager.getInstance().handlerResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        CropperManager.getInstance().destroy()
    }



    fun pickFromCamera() {
        CropperManager.getInstance().pickFromCamera(iv_cropped)
    }

    fun pickFromGallery() {
        CropperManager.getInstance().pickFromGallery()
    }

    fun showRationaleForCamera(request: PermissionRequest) {
        showRationaleDialog("?????????????????????????????????????????????", request)
    }

    fun showRationaleForPick(request: PermissionRequest) { //????????????
        showRationaleDialog("?????????????????????????????????????????????", request)
    }


    private fun showRationaleDialog(msg: String, request: PermissionRequest) {
//        AlertDialog.Builder(activity)
//                .setPositiveButton("??????", DialogInterface.OnClickListener { dialog, which -> request.proceed() })
//                .setNegativeButton("??????", DialogInterface.OnClickListener { dialog, which -> request.cancel() })
//                .setCancelable(false)
//                .setMessage(msg)
//                .show()
    }
}