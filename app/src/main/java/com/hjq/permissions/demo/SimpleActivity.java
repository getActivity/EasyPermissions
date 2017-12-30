package com.hjq.permissions.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hjq.permissions.bean.Permission;
import com.hjq.permissions.request.SimplePermissions;
import com.hjq.permissions.annotations.HasPermission;
import com.hjq.permissions.annotations.NoPermission;

public class SimpleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(this.getClass().getSimpleName());

        requestFilePermissions();
    }

    //请求的权限组
    private static final String[] requestPermission = {Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};

    //动态申请文件读写权限
    public void requestFilePermissions() {
        SimplePermissions.request(SimpleActivity.this, requestPermission);
    }

    @HasPermission
    public void openFileSucceed() {
        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @NoPermission
    public void openFileFail() {
        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
    }

    //必须覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调权限请求框架，多个权限请求只需要调用一次
        SimplePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    //如果需要参数可以使用这两个方法，不能和上面的两个无参方法一起使用
//    @HasPermission
//    public void openFileSucceed(List<PermissionInfo> granted) {
//        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限成功，有" + granted.size() + "个权限请求成功", Toast.LENGTH_SHORT).show();
//    }
//
//    @NoPermission
//    public void openFileFail(List<PermissionInfo> denied, boolean quick) {
//        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限失败，有" + denied.size() + "个权限请求失败", Toast.LENGTH_SHORT).show();
//    }
}
