package com.sample.librarypermission;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.sample.mylibrary.SDKPermission;
import com.sample.mylibrary.callback.CallbackPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CallbackPermission {
    private static final String TAG = MainActivity.class.getName();
    private String[] arrayPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        checkPermission();
    }

    private void checkPermission() {
        SDKPermission.getInstance()
                .checkPermission()
                .setCallbackPermission(this)
                .setListPermission(arrayPermission)
                .startCheckPermission();

    }

    @Override
    public void onPermissionGranted() {
        Log.d(TAG, "onPermissionGranted: Check permission Success .");
    }

    @Override
    public void onPermissionDenied(List<String> deniedPermissions) {
        Toast.makeText(this, "Permission False !", Toast.LENGTH_SHORT).show();
        checkPermission();
    }

    @Override
    public void onBackPressedPermission() {

    }
}