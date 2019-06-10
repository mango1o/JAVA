package data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import util.BitmapUtil;
import util.MetricsUtil;
import util.Utils;

public class CameraView extends SurfaceView{
    private static final String TAG = "CameraView";
    private Context mContext;
    private Camera mCamera;
    private SurfaceHolder mHolder = null;
    private boolean isPreviewing = false;
    private Point mCameraSize;
    private int mCameraType = CAMERA_BEHIND;
    public static int CAMERA_BEHIND = 0;
    public static int CAMERA_FRONT = 1;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(mSurfaceCallback);
    }

//    public int getCameraType() {
//        return mCameraType;
//    }

    public void setCameraType(int CameraType) {
        mCameraType = CameraType;
    }

    private String mPhotoPath;

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public void doTakePicture() {
        if (isPreviewing && mCamera != null) {
            mCamera.takePicture(mShutterCallback, null, mPictureCallback);
        }
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap raw = null;
            if (null != data) {
                raw = BitmapFactory.decodeByteArray(data, 0, data.length);
                mCamera.stopPreview();
                isPreviewing = false;
            }
            Bitmap bitmap = BitmapUtil.getRotateBitmap(raw, (mCameraType == CAMERA_BEHIND) ? 90 : -90);
            mPhotoPath = String.format("%s%s.jpg", BitmapUtil.getCachePath(mContext), Utils.getNowDateTime());
            BitmapUtil.saveBitMap(mPhotoPath, bitmap, "jpg", 80);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCamera = Camera.open(mCameraType);
            try {
                mCamera.setPreviewDisplay(holder);
                mCameraSize = MetricsUtil.getCameraSize(mCamera.getParameters(), MetricsUtil.getSize(mContext));
                Log.d(TAG, "width="+mCameraSize.x+", height="+mCameraSize.y);
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mCameraSize.x, mCameraSize.y);
                parameters.setPictureSize(mCameraSize.x, mCameraSize.y);
                parameters.setPictureFormat(ImageFormat.JPEG);
                if (mCameraType == CameraView.CAMERA_BEHIND) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                }
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
                mCamera.release();
                mCamera = null;
            }
            return;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            isPreviewing = true;
            mCamera.autoFocus(null);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    };

}
