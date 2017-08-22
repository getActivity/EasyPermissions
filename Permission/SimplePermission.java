package com.hjq.md.Permission;

import android.Manifest;

/**
 * Created by HJQ on 2017-5-9.
 */

public class SimplePermission {

    private Object mObject;
    private int mRequestCode;
    private String[] mRequestPermissions;

    /**
     * 创建一个权限请求对象
     * @param object            Activity或Fragment对象
     * @param requestCode       本次请求码
     * @param permissions       请求的权限数组
     */
    public SimplePermission(Object object, int requestCode, String[] permissions){
        include(object).setRequestCode(requestCode).setRequestPermissions(permissions).start();
    }

    /**
     * 设置请求的对象
     */
    public SimplePermission include(Object object){
        if (object == null) {
            throw new NullPointerException("必须传入Activity或Fragment");
        }else if (!PermissionUtils.isCorrectObject(object)){
            throw new IllegalArgumentException(object.getClass().getSimpleName() + "这个对象不是Activity或Fragment");
        }
        mObject = object;
        return this;
    }

    /**
     * 设置请求码
     */
    public SimplePermission setRequestCode(int code){
        if (code >= 65536){
            throw new IllegalArgumentException("请求码必须小于65536");
        }
        this.mRequestCode = code;
        return this;
    }

    /**
     * 设置请求的权限数组
     */
    public SimplePermission setRequestPermissions(String... permissions){
        this.mRequestPermissions = permissions;
        return this;
    }


    /**
     * 发起权限请求
     */
    public SimplePermission start() {
        //如果版本不是6.0及以上,通过注解的形式，反射执行方法
        if(!(PermissionUtils.isOverMarshmallow())){
            PermissionUtils.executeSucceesMethod(mObject,mRequestCode);
            return this;
        }

        String[] deniedPermissions = PermissionUtils.getDeniedPermissions(mObject, mRequestPermissions);

        if (deniedPermissions.length == 0){
            //证明权限已经全部授予过
            PermissionUtils.executeSucceesMethod(mObject,mRequestCode);
            return this;
        }else{
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(mObject, deniedPermissions, mRequestCode);
        }
        return this;
    }

    /**
     * 处理申请权限的回调
     */
    public void onRequestPermissionsResult(Object object, int requestCode, String[] permissions) {

        if (mRequestCode != requestCode || !PermissionUtils.equalStringArray(mRequestPermissions, permissions)) {
            return;
        }
        //再次获取没有授予的权限
        String[] deniedPermissions = PermissionUtils.getDeniedPermissions(object, permissions);
        if (deniedPermissions.length == 0){
            //代表申请的所有的权限都授予了
            PermissionUtils.executeSucceesMethod(object, requestCode);
        }else{
            //代表申请的权限中有不同意授予的
            PermissionUtils.executeFailMethod(object, requestCode, deniedPermissions);
        }
    }
}
