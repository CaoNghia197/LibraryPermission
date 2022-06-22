package com.sample.librarypermission

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sample.mylibrary.SDKPermission
import com.sample.mylibrary.callback.CallbackPermission

class MainActivity :CallbackPermission, AppCompatActivity() {
    private val TAG: String? = MainActivity::class.simpleName
    private var arrayPermission: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INSTALL_PACKAGES,
        Manifest.permission.DELETE_PACKAGES
    );

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        checkPermission()
    }

    override fun onPermissionGranted() {
        Toast.makeText(this,"Check Permission Success ! ",Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        Log.d(TAG, "onPermissionDenied: " + deniedPermissions.toString())
        checkPermission()
    }

    private fun checkPermission(){
        SDKPermission.getInstance().checkPermission()
            .setCallbackPermission(this)
            .setListPermission(*arrayPermission)
            .startCheckPermission()
    }
}