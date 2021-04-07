package com.jeanboy.cropview.cropper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.jeanboy.cropview.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Next on 2016/8/3.
 */
public class CropperManager {


    public static final String MSG_FILE_NOT_FOUND = "图片不可用";
    public static final String MSG_ERROR = "操作失败";

    private static CropperManager instance;

    private CropperHandler cropperHandler;

    private Uri cameraCacheUri;
    File pictureFile;

    private CropperManager() {
    }

    public static CropperManager getInstance() {
        if (instance == null) {
            synchronized (CropperManager.class) {
                if (instance == null) {
                    instance = new CropperManager();
                }
            }
        }
        return instance;
    }

    public void build(CropperHandler cropperHandler) {
        this.cropperHandler = cropperHandler;
    }

    public void destroy() {
        this.instance = null;
        this.cropperHandler = null;
    }

    public void pickFromCamera(ImageView iv_cropped) {
        if (cropperHandler == null) return;
        createCameraUri();//生成相机缓存文件

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            cropperHandler.getActivity().startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                            putExtra(MediaStore.EXTRA_OUTPUT, cameraCacheUri),
                    CropperParams.REQUEST_PICK_CAMERA);
        } else {
//            Log.e("----", "cameraCacheUri=" + cameraCacheUri.getPath());
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, cameraCacheUri.getPath());
//            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//            Uri uri = cropperHandler.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            //采用下面方法
            Uri uri = FileProvider.getUriForFile(cropperHandler.getActivity(), "com.s20cxq.testapp"+".fileprovider", pictureFile);
            Log.e("----","uri="+uri.toString());

//            try {
//                iv_cropped.setImageBitmap(MediaStore.Images.Media.getBitmap(cropperHandler.getActivity().getContentResolver(), uri));
////            mCropImageView.rotateImage(90);
//            } catch (IOException e) {
////                Log.e(TAG, e.getMessage());
//            }

            cropperHandler.getActivity().startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).
                    putExtra(MediaStore.EXTRA_OUTPUT, uri), CropperParams.REQUEST_PICK_CAMERA);

        }
    }

    public void pickFromGallery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            cropperHandler.getActivity().startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                    CropperParams.REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            cropperHandler.getActivity().startActivityForResult(intent, CropperParams.REQUEST_KITKAT_PICK_IMAGE);
        }
    }

    public void handlerResult(int requestCode, int resultCode, Intent result) {
        if (cropperHandler == null) return;
        if (resultCode == Activity.RESULT_CANCELED) {
            cropperHandler.onCropCancel();
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CropperParams.REQUEST_PICK_CAMERA://相机拍完照回调处理，去裁切
                    notifyImageToGallery(cropperHandler.getActivity(), cameraCacheUri);
                    cropperHandler.getActivity().startActivityForResult(new Intent(cropperHandler.getActivity(), CropActivity.class)
                                    .putExtra(CropperParams.PICK_URI, cameraCacheUri)
                                    .putExtra(CropperParams.ASPECT_X, cropperHandler.getParams().aspectX)
                                    .putExtra(CropperParams.ASPECT_Y, cropperHandler.getParams().aspectY),
                            CropperParams.REQUEST_CROPPED);
                    break;
                case CropperParams.REQUEST_PICK_IMAGE:
                    if (result != null) {
                        cropperHandler.getActivity().startActivityForResult(new Intent(cropperHandler.getActivity(), CropActivity.class)
                                        .putExtra(CropperParams.PICK_URI, result.getData())
                                        .putExtra(CropperParams.ASPECT_X, cropperHandler.getParams().aspectX)
                                        .putExtra(CropperParams.ASPECT_Y, cropperHandler.getParams().aspectY),
                                CropperParams.REQUEST_CROPPED);
                    } else {
                        cropperHandler.onCropFailed(MSG_FILE_NOT_FOUND);
                    }
                    break;
                case CropperParams.REQUEST_KITKAT_PICK_IMAGE:
                    if (result != null) {
                        cropperHandler.getActivity().startActivityForResult(new Intent(cropperHandler.getActivity(), CropActivity.class)
                                        .putExtra(CropperParams.PICK_URI, Utils.ensureUriPermission(cropperHandler.getActivity(), result))
                                        .putExtra(CropperParams.ASPECT_X, cropperHandler.getParams().aspectX)
                                        .putExtra(CropperParams.ASPECT_Y, cropperHandler.getParams().aspectY),
                                CropperParams.REQUEST_CROPPED);
                    } else {
                        cropperHandler.onCropFailed(MSG_FILE_NOT_FOUND);
                    }
                    break;
                case CropperParams.REQUEST_CROPPED:
                    if (result != null) {
                        Uri uri = result.getExtras().getParcelable(CropperParams.PICK_URI);
                        cropperHandler.onCropped(uri);
                    } else {
                        cropperHandler.onCropFailed(MSG_ERROR);
                    }
                    break;
            }
        }
    }

    public void createCameraUri() {
        //创建相机拍照文件保存目录，默认保存在/mnt/sdcard/Android/data/<包名>/cache
        String cameraFileName = getCameraFileName();
        cameraCacheUri = Uri.fromFile(cropperHandler.getActivity().getExternalCacheDir()).buildUpon().appendPath(cameraFileName)
                .build();

        pictureFile = new File(cropperHandler.getActivity().getExternalCacheDir(), cameraFileName);

    }

    private String getCameraFileName() {
        return "cropper_" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 同步图片到系统图库
     *
     * @param context
     * @param uri
     */
    public void notifyImageToGallery(Context context, Uri uri) {
        //把文件插入到系统图库
        try {
            File file = new File(uri.getPath());
            Log.e("----", "file.getAbsolutePath()=" + file.getAbsolutePath() + "  uri.getPath()=" + uri.getPath());
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }
}
