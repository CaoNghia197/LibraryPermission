package com.sample.mylibrary.actpermission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.sample.mylibrary.R;
import com.sample.mylibrary.basepermission.ObjectUtils;
import com.sample.mylibrary.basepermission.TedPermissionBase;
import com.sample.mylibrary.callback.CallbackPermission;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class ActivityTedPermission extends AppCompatActivity {
    public static final int REQ_CODE_PERMISSION_REQUEST = 10;

    public static final int REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST = 30;
    public static final int REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING = 31;


    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_RATIONALE_TITLE = "rationale_title";
    public static final String EXTRA_RATIONALE_MESSAGE = "rationale_message";
    public static final String EXTRA_DENY_TITLE = "deny_title";
    public static final String EXTRA_DENY_MESSAGE = "deny_message";
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_SETTING_BUTTON = "setting_button";
    public static final String EXTRA_SETTING_BUTTON_TEXT = "setting_button_text";
    public static final String EXTRA_RATIONALE_CONFIRM_TEXT = "rationale_confirm_text";
    public static final String EXTRA_DENIED_DIALOG_CLOSE_TEXT = "denied_dialog_close_text";
    private static Deque<CallbackPermission> permissionListenerStack;
    CharSequence rationaleTitle;
    CharSequence rationale_message;
    CharSequence denyTitle;
    CharSequence denyMessage;
    String[] permissions;
    String packageName;
    boolean hasSettingButton;
    String settingButtonText;
    String deniedCloseButtonText;
    String rationaleConfirmText;
    boolean isShownRationaleDialog;
    private Button btConfirm;
    private Button btSetting;
    private TextView tvTitle;
    private static CallbackPermission callback;

    public static void startActivity(Context context, Intent intent, CallbackPermission listener) {
        if (permissionListenerStack == null) {
            permissionListenerStack = new ArrayDeque<>();
        }
        permissionListenerStack.push(listener);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_permission);
        setupFromSavedInstanceState(savedInstanceState);
        btConfirm = findViewById(R.id.bt_confirm);
        btSetting = findViewById(R.id.bt_setting);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(R.string.aler_message_setting);
        callback = permissionListenerStack.getFirst();
        btConfirm.setOnClickListener(v -> {
            // check windows
            if (needWindowPermission()) {
                requestWindowPermission();
            } else {
                checkPermissions(false);
            }
        });
        btSetting.setOnClickListener(v -> TedPermissionBase.startSettingActivityForResult(ActivityTedPermission.this));
        checkAllowed();
    }

    private void checkAllowed() {
        int nonGrantedPermissions = 0;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions++;
            }
        }
        if (nonGrantedPermissions == 0) {
            callback.onPermissionGranted();
            finish();
        }
    }

    private void setupFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            permissions = savedInstanceState.getStringArray(EXTRA_PERMISSIONS);
            rationaleTitle = savedInstanceState.getCharSequence(EXTRA_RATIONALE_TITLE);
            rationale_message = savedInstanceState.getCharSequence(EXTRA_RATIONALE_MESSAGE);
            denyTitle = savedInstanceState.getCharSequence(EXTRA_DENY_TITLE);
            denyMessage = savedInstanceState.getCharSequence(EXTRA_DENY_MESSAGE);
            packageName = savedInstanceState.getString(EXTRA_PACKAGE_NAME);

            hasSettingButton = savedInstanceState.getBoolean(EXTRA_SETTING_BUTTON, true);

            rationaleConfirmText = savedInstanceState.getString(EXTRA_RATIONALE_CONFIRM_TEXT);
            deniedCloseButtonText = savedInstanceState.getString(EXTRA_DENIED_DIALOG_CLOSE_TEXT);

            settingButtonText = savedInstanceState.getString(EXTRA_SETTING_BUTTON_TEXT);
        } else {
            Intent intent = getIntent();
            permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS);
            rationaleTitle = intent.getCharSequenceExtra(EXTRA_RATIONALE_TITLE);
            rationale_message = intent.getCharSequenceExtra(EXTRA_RATIONALE_MESSAGE);
            denyTitle = intent.getCharSequenceExtra(EXTRA_DENY_TITLE);
            denyMessage = intent.getCharSequenceExtra(EXTRA_DENY_MESSAGE);
            packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
            hasSettingButton = intent.getBooleanExtra(EXTRA_SETTING_BUTTON, true);
            rationaleConfirmText = intent.getStringExtra(EXTRA_RATIONALE_CONFIRM_TEXT);
            deniedCloseButtonText = intent.getStringExtra(EXTRA_DENIED_DIALOG_CLOSE_TEXT);
            settingButtonText = intent.getStringExtra(EXTRA_SETTING_BUTTON_TEXT);
        }
    }

    private boolean needWindowPermission() {
        for (String permission : permissions) {
            if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                return !hasWindowPermission();
            }
        }
        return false;
    }

    @TargetApi(VERSION_CODES.M)
    private boolean hasWindowPermission() {
        return Settings.canDrawOverlays(getApplicationContext());
    }

    @TargetApi(VERSION_CODES.M)
    private void requestWindowPermission() {
        Uri uri = Uri.fromParts("package", packageName, null);
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);

        if (!TextUtils.isEmpty(rationale_message)) {
            new AlertDialog.Builder(this)
                    .setMessage(rationale_message)
                    .setCancelable(false)

                    .setNegativeButton(rationaleConfirmText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(intent, REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST);
                        }
                    })
                    .show();
            isShownRationaleDialog = true;
        } else {
            startActivityForResult(intent, REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST);
        }
    }

    private void checkPermissions(boolean fromOnActivityResult) {
        List<String> needPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (!hasWindowPermission()) {
                    needPermissions.add(permission);
                }
            } else {
                if (TedPermissionBase.isDenied(permission)) {
                    needPermissions.add(permission);
                }
            }
        }
        String [] arrayPermission = new String[needPermissions.size()];
        for (int i = 0; i < needPermissions.size(); i++){
            arrayPermission[i] = needPermissions.get(i);
        }
        if (needPermissions.isEmpty()) {
            permissionResult(null);
        } else if (fromOnActivityResult) { //From Setting Activity
            permissionResult(needPermissions);
        } else if (needPermissions.size() == 1 && needPermissions
                .contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {   // window permission deny
            permissionResult(needPermissions);
        } else { // //Need Request Permissions
            requestPermissions(arrayPermission);
        }
    }

    private void permissionResult(List<String> deniedPermissions) {
        finish();
        overridePendingTransition(0, 0);

        if (permissionListenerStack != null) {
            CallbackPermission listener = permissionListenerStack.pop();

            if (ObjectUtils.isEmpty(deniedPermissions))
                listener.onPermissionGranted();
            else {
                listener.onPermissionDenied(deniedPermissions);
                onBackPressed();
            }
            if (permissionListenerStack.size() == 0) {
                permissionListenerStack = null;
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public void requestPermissions(String[]needPermissions) {
        ActivityCompat.requestPermissions(this, needPermissions,
                REQ_CODE_PERMISSION_REQUEST);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(EXTRA_PERMISSIONS, permissions);
        outState.putCharSequence(EXTRA_RATIONALE_TITLE, rationaleTitle);
        outState.putCharSequence(EXTRA_RATIONALE_MESSAGE, rationale_message);
        outState.putCharSequence(EXTRA_DENY_TITLE, denyTitle);
        outState.putCharSequence(EXTRA_DENY_MESSAGE, denyMessage);
        outState.putString(EXTRA_PACKAGE_NAME, packageName);
        outState.putBoolean(EXTRA_SETTING_BUTTON, hasSettingButton);
        outState.putString(EXTRA_DENIED_DIALOG_CLOSE_TEXT, deniedCloseButtonText);
        outState.putString(EXTRA_RATIONALE_CONFIRM_TEXT, rationaleConfirmText);
        outState.putString(EXTRA_SETTING_BUTTON_TEXT, settingButtonText);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> deniedPermissions = TedPermissionBase.getDeniedPermissions(Arrays.asList(permissions));

        if (deniedPermissions.isEmpty()) {
            callback.onPermissionGranted();
            finish();
        } else {
            showPermissionDenyDialog(deniedPermissions);
        }
    }

    public void showPermissionDenyDialog(final List<String> deniedPermissions) {
        if (TextUtils.isEmpty(denyMessage)) {
            permissionResult(deniedPermissions);
            return;
        }
        tvTitle.setText(R.string.aler_message);
        btConfirm.setVisibility(View.GONE);
        btSetting.setVisibility(View.VISIBLE);
    }

    public boolean shouldShowRequestPermissionRationale(List<String> needPermissions) {

        if (needPermissions == null) {
            return false;
        }

        for (String permission : needPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivityTedPermission.this, permission)) {
                return false;
            }
        }

        return true;

    }

    public void showWindowPermissionDenyDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityTedPermission.this);
        builder.setMessage(denyMessage)
                .setCancelable(false)
                .setNegativeButton(deniedCloseButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkPermissions(false);
                    }
                });

        if (hasSettingButton) {
            if (TextUtils.isEmpty(settingButtonText)) {
                settingButtonText = getString(R.string.setting);
            }

            builder.setPositiveButton(settingButtonText, new DialogInterface.OnClickListener() {
                @TargetApi(VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.fromParts("package", packageName, null);
                    final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri);
                    startActivityForResult(intent, REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING);
                }
            });

        }
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TedPermissionBase.REQ_CODE_REQUEST_SETTING:
                checkPermissions(true);
                break;
            case REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST:
                if (!hasWindowPermission() && !TextUtils.isEmpty(denyMessage)) {
                    showWindowPermissionDenyDialog();
                } else {
                    checkPermissions(false);
                }
                break;
            case REQ_CODE_SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_SETTING:
                checkPermissions(false);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callback.onBackPressedPermission();
    }
}
