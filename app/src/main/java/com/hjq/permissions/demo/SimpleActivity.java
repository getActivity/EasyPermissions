package com.hjq.permissions.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hjq.permissions.SimplePermissions;
import com.hjq.permissions.annotations.HasPermission;
import com.hjq.permissions.annotations.NoPermission;

public class SimpleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(this.getClass().getSimpleName());

        requestFilePermissions();
    }

    //本次权限请求码
    private static final int requestCode = 100;

    //请求的权限组
    private static final String[] requestPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //动态申请文件读写权限
    public void requestFilePermissions() {
        SimplePermissions.request(SimpleActivity.this, requestCode, requestPermission);
    }

    @HasPermission(requestCode)
    public void openFileSucceed() {
        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @NoPermission(requestCode)
    public void openFileFail() {
        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
    }

    //必须覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调权限请求框架，多个权限请求只需要调用一次
        SimplePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

//    //如果需要参数可以使用这两个方法，不能和上面的两个无参方法一起使用
//    @HasPermission(requestCode)
//    public void openFileSucceed(List<String> granted) {
//        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限成功，有" + granted.size() + "个权限请求成功", Toast.LENGTH_SHORT).show();
//    }
//
//    @NoPermission(requestCode)
//    public void openFileFail(List<String> denied) {
//        Toast.makeText(SimpleActivity.this, "获取SD卡读取写入权限失败，有" + denied.size() + "个权限请求失败", Toast.LENGTH_SHORT).show();
//    }
}
