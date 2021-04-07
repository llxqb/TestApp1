package com.s20cxq.testapp

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import camerademo.TakePhoteActivity
import com.jeanboy.cropview.cropper.CropperManager
import com.permissionx.guolindev.PermissionX


class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun takePhote(view: View?) {
        val permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission(permissions)
        }
    }

    fun checkPermission(permissions: List<String>){
        PermissionX.init(this)
            .permissions(permissions)
            .explainReasonBeforeRequest()
            .onForwardToSettings { scope, deniedList ->
//                    val message = "您需要去设置中手动开启以下权限"
//                    val dialog = CustomDialog(this, message, deniedList)
//                    scope.showForwardToSettingsDialog(dialog)
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
//                    Toast.makeText(activity, "所有申请的权限都已通过", Toast.LENGTH_SHORT).show()
//                        permissionListener?.permissionSuccess()
                    startActivity(Intent(this, TakePhoteActivity::class.java))
                } else {
                    Toast.makeText(this, "您需要运行所有权限才能运行", Toast.LENGTH_SHORT).show()
//                        permissionListener?.permissionFail()
                }
            }
    }

}