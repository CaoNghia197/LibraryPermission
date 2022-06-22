package com.sample.mylibrary.basepermission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.sample.mylibrary.R;
import com.sample.mylibrary.actpermission.ActivityTedPermission;
import com.sample.mylibrary.callback.CallbackPermission;



public abstract class PermissionBuilder<T extends PermissionBuilder> {

    private static final Context context = TedPermissionProvider.context;
    private CallbackPermission listener;
    private String[] permissions;
    private CharSequence rationaleTitle;
    private CharSequence rationaleMessage;
    private CharSequence denyTitle;
    private CharSequence denyMessage;
    private CharSequence settingButtonText;
    private boolean hasSettingBtn = true;
    private CharSequence deniedCloseButtonText;
    private CharSequence rationaleConfirmText;

    @SuppressLint("ObsoleteSdkInt")
    protected void checkPermissions() {
        if (listener == null) {
            Toast.makeText(context.getApplicationContext(), "CallbackPermission Null ", Toast.LENGTH_SHORT).show();
            return;
        } else if (ObjectUtils.isEmpty(permissions)) {
            Toast.makeText(context.getApplicationContext(), "List Permission Null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            listener.onPermissionGranted();
            return;
        }

        Intent intent = new Intent(context, ActivityTedPermission.class);
        intent.putExtra(ActivityTedPermission.EXTRA_PERMISSIONS, permissions);

        intent.putExtra(ActivityTedPermission.EXTRA_RATIONALE_TITLE, rationaleTitle == null ? context.getString(R.string.rationale_title) : rationaleTitle);
        intent.putExtra(ActivityTedPermission.EXTRA_RATIONALE_MESSAGE, rationaleMessage == null?context.getString(R.string.rationale_message):rationaleMessage);
        intent.putExtra(ActivityTedPermission.EXTRA_DENY_TITLE, denyTitle== null?context.getString(R.string.rationale_title):denyTitle);
        intent.putExtra(ActivityTedPermission.EXTRA_DENY_MESSAGE, denyMessage== null?context.getString(R.string.aler_message):denyMessage);
        intent.putExtra(ActivityTedPermission.EXTRA_PACKAGE_NAME, context.getPackageName());
        intent.putExtra(ActivityTedPermission.EXTRA_SETTING_BUTTON, hasSettingBtn);
        intent.putExtra(ActivityTedPermission.EXTRA_DENIED_DIALOG_CLOSE_TEXT, deniedCloseButtonText== null?context.getString(R.string.back):deniedCloseButtonText);
        intent.putExtra(ActivityTedPermission.EXTRA_RATIONALE_CONFIRM_TEXT, rationaleConfirmText== null?context.getString(R.string.tedpermission_confirm):rationaleConfirmText);
        intent.putExtra(ActivityTedPermission.EXTRA_SETTING_BUTTON_TEXT, settingButtonText== null?context.getString(R.string.setting):settingButtonText);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        ActivityTedPermission.startActivity(context, intent, listener);
        TedPermissionBase.setFirstRequest(permissions);
    }

    public T setCallbackPermission(CallbackPermission listener) {
        this.listener = listener;
        return (T) this;
    }

    public T setListPermission(String... permissions) {
        this.permissions = permissions;
        return (T) this;
    }

    public T setMessagePermission(@StringRes int stringRes) {
        return setMessagePermission(getText(stringRes));
    }

    private CharSequence getText(@StringRes int stringRes) {
        return context.getText(stringRes);
    }

    public T setMessagePermission(CharSequence rationaleMessage) {
        this.rationaleMessage = rationaleMessage;
        return (T) this;
    }


    public T setTitlePermission(@StringRes int stringRes) {
        return setTitlePermission(getText(stringRes));
    }

    public T setTitlePermission(CharSequence rationaleMessage) {
        this.rationaleTitle = rationaleMessage;
        return (T) this;
    }

    public T setMessageDialogAler(@StringRes int stringRes) {
        return setMessageDialogAler(getText(stringRes));
    }

    public T setMessageDialogAler(CharSequence denyMessage) {
        this.denyMessage = denyMessage;
        return (T) this;
    }

    public T setTitleDialogAler(@StringRes int stringRes) {
        return setTitleDialogAler(getText(stringRes));
    }

    public T setTitleDialogAler(CharSequence denyTitle) {
        this.denyTitle = denyTitle;
        return (T) this;
    }

    public T setGotoSettingButton(boolean hasSettingBtn) {
        this.hasSettingBtn = hasSettingBtn;
        return (T) this;
    }

    public T setButtonSettingDialogAler(@StringRes int stringRes) {
        return setButtonSettingDialogAler(getText(stringRes));
    }

    public T setButtomAgreePermission(@StringRes int stringRes) {
        return setButtomAgreePermission(getText(stringRes));
    }

    public T setButtomAgreePermission(CharSequence rationaleConfirmText) {
        this.rationaleConfirmText = rationaleConfirmText;
        return (T) this;
    }

    public T setButtonSettingDialogAler(CharSequence rationaleConfirmText) {
        this.settingButtonText = rationaleConfirmText;
        return (T) this;
    }

    public T setButtomCloseDialogAler(CharSequence deniedCloseButtonText) {
        this.deniedCloseButtonText = deniedCloseButtonText;
        return (T) this;
    }

    public T setButtomCloseDialogAler(@StringRes int stringRes) {
        return setButtomCloseDialogAler(getText(stringRes));
    }

}
