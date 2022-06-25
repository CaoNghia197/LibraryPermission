package com.sample.mylibrary.callback;


import java.util.List;

public interface CallbackPermission {
    void onPermissionGranted();

    void onPermissionDenied(List<String> deniedPermissions);

    void onBackPressedPermission();
}
