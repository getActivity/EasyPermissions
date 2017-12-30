package com.hjq.permissions.base;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.hjq.permissions.request.EasyPermissions;
import com.hjq.permissions.request.SimplePermissions;

/**
 * Created by HJQ on 2017-10-5.
 *
 * Activity权限基类
 */

public abstract class PermissionActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        SimplePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
