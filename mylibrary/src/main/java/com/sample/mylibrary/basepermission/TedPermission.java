package com.sample.mylibrary.basepermission;

public class TedPermission extends TedPermissionBase {

    public static class Builder extends PermissionBuilder<Builder> {

        public void startCheckPermission() {
            checkPermissions();
        }
    }
}
