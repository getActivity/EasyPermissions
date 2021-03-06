package com.hjq.permissions.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.hjq.permissions.bean.Permission;
import com.hjq.permissions.request.EasyPermissions;
import com.hjq.permissions.call.OnRequestCallBack;
import com.hjq.permissions.request.PermissionUtils;
import com.hjq.permissions.bean.PermissionInfo;

import java.util.List;

public class EasyActivity extends AppCompatActivity {

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

        EasyPermissions.request(EasyActivity.this, new OnRequestCallBack() {

            @Override
            public void hasPermission(List<PermissionInfo> granted) {
                Toast.makeText(EasyActivity.this, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noPermission(List<PermissionInfo> denied, boolean quick) {
                if(quick) {
                    Toast.makeText(EasyActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                    PermissionUtils.gotoPermissionSettings(EasyActivity.this);
                }else {
                    Toast.makeText(EasyActivity.this, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
                }
            }

        }, requestPermission);
    }

    //必须覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调权限请求框架，多个权限请求只需要调用一次
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
