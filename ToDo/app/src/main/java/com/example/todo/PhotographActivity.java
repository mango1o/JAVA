package com.example.todo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;

import data.CameraView;
import data.ShootingAdapter;

public class PhotographActivity extends AppCompatActivity implements OnClickListener{
    private static final String TAG = "PhotographActivity";
    private FrameLayout fl_content;
    private ImageView iv_photo;
    private GridView gv_shooting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photograph);

        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        gv_shooting = (GridView) findViewById(R.id.gv_shooting);
        findViewById(R.id.btn_catch_behind).setOnClickListener(this);
        findViewById(R.id.btn_catch_front).setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i("TEST","Granted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult. requestCode="+requestCode+", resultCode="+resultCode);
        Bundle resp = intent.getExtras();
        String is_null = resp.getString("is_null");
        if (is_null.equals("yes") != true) {
            int type = resp.getInt("type");
            Log.d(TAG, "type="+type);
            if (type == 0) {
                iv_photo.setVisibility(View.VISIBLE);
                gv_shooting.setVisibility(View.GONE);
                String path = resp.getString("path");
                fillBitmap(BitmapFactory.decodeFile(path, null));
            } else if (type == 1) {
                iv_photo.setVisibility(View.GONE);
                gv_shooting.setVisibility(View.VISIBLE);
                ArrayList<String> pathList = resp.getStringArrayList("path_list");
                Log.d(TAG, "pathList.size()="+pathList.size());
                ShootingAdapter adapter = new ShootingAdapter(this, pathList);
                gv_shooting.setAdapter(adapter);
            }
        }
    }

    private void fillBitmap(Bitmap bitmap) {
        Log.d(TAG, "fillBitmap width="+bitmap.getWidth()+",height="+bitmap.getHeight());
        if (bitmap.getHeight() > fl_content.getMeasuredHeight()) {
            LayoutParams params = iv_photo.getLayoutParams();
            params.height = fl_content.getMeasuredHeight();
            params.width = bitmap.getWidth()*fl_content.getMeasuredHeight()/bitmap.getHeight();
            iv_photo.setLayoutParams(params);
        }
        iv_photo.setDrawingCacheEnabled(true);
        iv_photo.setScaleType(ScaleType.FIT_CENTER);
        iv_photo.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_catch_behind) {
            Camera mCamera = Camera.open();
            if (mCamera != null) {
                Log.i("CAMERA BEHIND", "normal");
                mCamera.release();
                Intent intent = new Intent(this, TakePictureActivity.class);
                intent.putExtra("type", CameraView.CAMERA_BEHIND);
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(this, "No behind camera support", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_catch_front) {
            Log.d(TAG, "getNumberOfCameras="+Camera.getNumberOfCameras());
            Camera mCamera = Camera.open(CameraView.CAMERA_FRONT);
            if (mCamera != null) {
                mCamera.release();
                Intent intent = new Intent(this, TakePictureActivity.class);
                intent.putExtra("type", CameraView.CAMERA_FRONT);
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(this, "No front camera support", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
