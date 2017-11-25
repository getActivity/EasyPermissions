package com.hjq.permissions.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.hjq.permissions.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(this.getClass().getSimpleName());
    }

    public void onClick1(View view) {
        startActivity(new Intent(MainActivity.this, EasyActivity.class));
    }

    public void onClick2(View view) {
        startActivity(new Intent(MainActivity.this, SimpleActivity.class));
    }

    public void onClick3(View view) {
        startActivity(new Intent(MainActivity.this, RequestActivity.class));
    }

    public void onClick4(View view){
        //请求的权限组
        String[] requestPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (PermissionUtils.isHasPermission(MainActivity.this, requestPermission)) {
            Toast.makeText(MainActivity.this, "已经获取到SD卡读写权限，不需要再次申请了", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "还没有获取到SD卡读写权限", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick5(View view) {
        PermissionUtils.gotoPermissionSettings(MainActivity.this);
    }

}
