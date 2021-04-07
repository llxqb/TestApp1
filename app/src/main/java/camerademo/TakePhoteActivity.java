package camerademo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.jeanboy.cropview.cropper.CropActivity;
import com.jeanboy.cropview.cropper.CropperHandler;
import com.jeanboy.cropview.cropper.CropperManager;
import com.jeanboy.cropview.cropper.CropperParams;
import com.s20cxq.testapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import camerademo.camare.CameraPreview;
import camerademo.camare.FocusView;
import camerademo.cropper.CropImageView;
import camerademo.utils.Utils;

/**
 * @Class: TakePhoteActivity
 * @Description: ���ս���
 * @author: lling(www.cnblogs.com/liuling)
 * @Date: 2015/10/25
 */
public class TakePhoteActivity extends Activity implements CameraPreview.OnCameraStatusListener,
        SensorEventListener,CropperHandler {
    private static final String TAG = "TakePhoteActivity";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String PATH = Environment.getExternalStorageDirectory()
            .toString() + "/AndroidMedia/";
    CameraPreview mCameraPreview;
    CropImageView mCropImageView;
    RelativeLayout mTakePhotoLayout;
    LinearLayout mCropperLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_phote);
        // Initialize components of the app
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        FocusView focusView = (FocusView) findViewById(R.id.view_focus);
        mTakePhotoLayout = (RelativeLayout) findViewById(R.id.take_photo_layout);
        mCropperLayout = (LinearLayout) findViewById(R.id.cropper_layout);

        mCameraPreview.setFocusView(focusView);
        mCameraPreview.setOnCameraStatusListener(this);
        mCropImageView.setGuidelines(2);

        mSensorManager = (SensorManager) getSystemService(Context.
                SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);

    }

    boolean isRotated = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(!isRotated) {
            TextView hint_tv = (TextView) findViewById(R.id.hint);
            ObjectAnimator animator = ObjectAnimator.ofFloat(hint_tv, "rotation", 0f, 90f);
            animator.setStartDelay(800);
            animator.setDuration(1000);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
            View view =  findViewById(R.id.crop_hint);
            AnimatorSet animSet = new AnimatorSet();
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "rotation", 0f, 90f);
            ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", 0f, -50f);
            animSet.play(animator1).before(moveIn);
            animSet.setDuration(10);
            animSet.start();
            isRotated = true;
        }
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    //开始拍照
    public void takePhoto(View view) {
        if(mCameraPreview != null) {
            mCameraPreview.takePicture();
        }
    }

    public void close(View view) {
        finish();
    }

    /**
     * �رս�ͼ����
     * @param view
     */
    public void closeCropper(View view) {
        showTakePhotoLayout();
    }

    /**
     * ��ʼ��ͼ��������ͼƬ
     * @param view
     */
    public void startCropper(View view) {
        //��ȡ��ͼ����ת90��
        Log.i(TAG, "startCropper()");
        CropperImage cropperImage = mCropImageView.getCroppedImage();
        Log.e(TAG, cropperImage.getX() + "," + cropperImage.getY());
        Log.e(TAG, cropperImage.getWidth() + "," + cropperImage.getHeight());
        Bitmap bitmap = Utils.rotate(cropperImage.getBitmap(), -90);
//        Bitmap bitmap = mCropImageView.getCroppedImage();
        // ϵͳʱ��
        long dateTaken = System.currentTimeMillis();
        // ͼ������
//        String filename = DateFormat.format("yyyy-MM-dd kk.mm.ss", dateTaken)
//                .toString() + ".jpg";
        String filename = "cropper_" + System.currentTimeMillis() + ".jpg";
//        Uri uri = insertImage(getContentResolver(), filename, dateTaken, PATH,
//                filename, bitmap, null);
        Uri uri = insertImage( filename, bitmap, null);

        cropperImage.getBitmap().recycle();
        cropperImage.setBitmap(null);
        Intent intent = new Intent(this, CropActivity.class);
        intent.setData(uri);
//        intent.putExtra("path", PATH + filename);
//        intent.putExtra("width", bitmap.getWidth());
//        intent.putExtra("height", bitmap.getHeight());
//        intent.putExtra("cropperImage", cropperImage);
        intent.putExtra(CropperParams.PICK_URI, cameraCacheUri)
                .putExtra(CropperParams.ASPECT_X, getParams().aspectX)
                .putExtra(CropperParams.ASPECT_Y, getParams().aspectY);
        startActivity(intent);
        bitmap.recycle();
        finish();
        super.overridePendingTransition(R.anim.fade_in,
                R.anim.fade_out);
//        doAnimation(cropperImage);
    }

    private void doAnimation(CropperImage cropperImage) {
        ImageView imageView = new ImageView(this);
        View view = LayoutInflater.from(this).inflate(
                R.layout.image_view_layout, null);
        ((RelativeLayout) view.findViewById(R.id.root_layout)).addView(imageView);
        RelativeLayout relativeLayout = ((RelativeLayout) findViewById(R.id.root_layout));
//        relativeLayout.addView(imageView);
        imageView.setX(cropperImage.getX());
        imageView.setY(cropperImage.getY());
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = (int)cropperImage.getWidth();
        lp.height = (int) cropperImage.getHeight();
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(cropperImage.getBitmap());
        try {
            getWindow().addContentView(view, lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator translationX = ObjectAnimator.ofFloat(this, "translationX", cropperImage.getX(), 0);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(this, "translationY", cropperImage.getY(), 0);*/

        TranslateAnimation translateAnimation = new TranslateAnimation(
                0, -cropperImage.getX(), 0, -(Math.abs(cropperImage.getHeight() - cropperImage.getY())));// ��ǰλ���ƶ���ָ��λ��
        RotateAnimation rotateAnimation = new RotateAnimation(0, -90,
                Animation.ABSOLUTE, cropperImage.getX() ,Animation.ABSOLUTE, cropperImage.getY());
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.setFillAfter(true);
        animationSet.setDuration(2000L);
        imageView.startAnimation(animationSet);
//        finish();
    }

    /**
     * ���ճɹ���ص�
     * �洢ͼƬ����ʾ��ͼ����
     * @param data
     */
    @Override
    public void onCameraStopped(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        String filename = "cropper_" + System.currentTimeMillis() + ".jpg";
        cameraCacheUri = Uri.fromFile(this.getExternalCacheDir()).buildUpon().appendPath(filename)
                .build();

        Uri uri = insertImage( filename, bitmap, data);
        try {
            mCropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
//            mCropImageView.rotateImage(90);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        notifyImageToGallery(this, cameraCacheUri);
        Intent intent = new Intent(this, CropActivity.class)
                .putExtra(CropperParams.PICK_URI, cameraCacheUri)
                .putExtra(CropperParams.ASPECT_X, getParams().aspectX)
                .putExtra(CropperParams.ASPECT_Y, getParams().aspectY);
        startActivityForResult(intent, CropperParams.REQUEST_CROPPED);

//        showCropperLayout();
    }

    public static final String MSG_FILE_NOT_FOUND = "图片不可用";
    public static final String MSG_ERROR = "操作失败";
    private Uri cameraCacheUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.e(TAG,"resultCode="+resultCode);
        if (resultCode == Activity.RESULT_CANCELED) {
           onCropCancel();
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CropperParams.REQUEST_PICK_CAMERA://相机拍完照回调处理，去裁切
                    notifyImageToGallery(this, cameraCacheUri);
                    startActivityForResult(new Intent(this, CropActivity.class)
                                    .putExtra(CropperParams.PICK_URI, cameraCacheUri)
                                    .putExtra(CropperParams.ASPECT_X, getParams().aspectX)
                                    .putExtra(CropperParams.ASPECT_Y, getParams().aspectY),
                            CropperParams.REQUEST_CROPPED);
                    break;
                case CropperParams.REQUEST_PICK_IMAGE:
                    if (result != null) {
                        startActivityForResult(new Intent(this, CropActivity.class)
                                        .putExtra(CropperParams.PICK_URI, result.getData())
                                        .putExtra(CropperParams.ASPECT_X, getParams().aspectX)
                                        .putExtra(CropperParams.ASPECT_Y,getParams().aspectY),
                                CropperParams.REQUEST_CROPPED);
                    } else {
                        onCropFailed(MSG_FILE_NOT_FOUND);
                    }
                    break;
                case CropperParams.REQUEST_KITKAT_PICK_IMAGE:
                    if (result != null) {
                        startActivityForResult(new Intent(this, CropActivity.class)
                                        .putExtra(CropperParams.PICK_URI, com.jeanboy.cropview.util.Utils.ensureUriPermission(this, result))
                                        .putExtra(CropperParams.ASPECT_X, getParams().aspectX)
                                        .putExtra(CropperParams.ASPECT_Y, getParams().aspectY),
                                CropperParams.REQUEST_CROPPED);
                    } else {
                        onCropFailed(MSG_FILE_NOT_FOUND);
                    }
                    break;
                case CropperParams.REQUEST_CROPPED:
                    if (result != null) {
                        Uri uri = result.getExtras().getParcelable(CropperParams.PICK_URI);
                        onCropped(uri);
                    } else {
                        onCropFailed(MSG_ERROR);
                    }
                    break;
            }
        }
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



    /**
     * �洢ͼ�񲢽���Ϣ�����ý�����ݿ�
     */
    private Uri insertImage(String filename, Bitmap source, byte[] jpegData) {
        OutputStream outputStream = null;

//        File pictureFile = new File(this.getExternalCacheDir(), filename);
        File file;
        try {
            file = new File(this.getExternalCacheDir(), filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(jpegData);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }

        Uri uri = FileProvider.getUriForFile(this, "com.s20cxq.testapp"+".fileprovider", file);
//        Log.e("----","uri="+uri.toString());
//        ContentValues values = new ContentValues(7);
//        values.put(MediaStore.Images.Media.TITLE, name);
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
//        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.Images.Media.DATA, filePath);
//        Uri uri = cr.insert(IMAGE_URI, values);
        Log.e("----","uri="+uri.toString());
        return uri;
//        return uri;
    }

    private void showTakePhotoLayout() {
        mTakePhotoLayout.setVisibility(View.VISIBLE);
        mCropperLayout.setVisibility(View.GONE);
    }

    private void showCropperLayout() {
        mTakePhotoLayout.setVisibility(View.GONE);
        mCropperLayout.setVisibility(View.VISIBLE);
        mCameraPreview.start();   //������������ͷ
    }


    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private boolean mInitialized = false;
    private SensorManager mSensorManager;
    private Sensor mAccel;
    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized){
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX  = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if(deltaX > 0.8 || deltaY > 0.8 || deltaZ > 0.8){
            mCameraPreview.setFocus();
        }
        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public CropperParams getParams() {
        return new CropperParams(1, 1);
    }

    @Override
    public void onCropped(Uri uri) {
        try {
            mCropImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
//            mCropImageView.rotateImage(90);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onCropCancel() {

    }

    @Override
    public void onCropFailed(String msg) {

    }
}
