package com.sample.mylibrary;

import com.sample.mylibrary.basepermission.TedPermission;


public class SDKPermission {
    private static SDKPermission instance;

    public static SDKPermission getInstance() {
        if (instance == null) {
            instance = new SDKPermission();
        }
        return instance;
    }

    public TedPermission.Builder checkPermission() {
        return new TedPermission.Builder();
    }
}
