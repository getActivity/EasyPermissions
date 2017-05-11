package com.hjq.md.Permission;

import java.util.Random;

/**
 * Created by HJQ on 2017-5-9.
 */

public class EasyPermission {

    private int mRequestCode;
    private String[] mRequestPermissions;
    private EasyRequestBackCall mRequestBackCall;

    /**
     * 创建一个权限请求对象
     * @param object            Activity或Fragment对象
     * @param permissions       请求的权限数组
     * @param requestBackCall   用于接收结果的回调
     */
    public EasyPermission(Object object, String[] permissions, EasyRequestBackCall requestBackCall){
        if (object == null) {
            throw new NullPointerException("必须传入Activity或Fragment");
        }else if (!PermissionUtils.isCorrectObject(object)){
            throw new IllegalArgumentException(object.getClass().getSimpleName() + "这个对象不是Activity或Fragment");
        }

        if (requestBackCall == null) {
            throw new NullPointerException("权限请求回调接口没有被实现");
        }else{
            this.mRequestBackCall = requestBackCall;
        }
        this.mRequestPermissions = permissions;
        this.mRequestCode = new Random().nextInt(65535);

        //如果版本不是6.0及以上,通过注解的形式，反射执行方法
        if(!(PermissionUtils.isOverMarshmallow())){
            mRequestBackCall.requestSucceed();
            return;
        }

        String[] deniedPermissions = PermissionUtils.getDeniedPermissions(object, permissions);

        if (deniedPermissions.length == 0){
            //证明权限已经全部授予过
            mRequestBackCall.requestSucceed();
            return;
        }else{
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(object, deniedPermissions, mRequestCode);
        }
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
            mRequestBackCall.requestSucceed();
        }else{
            //代表申请的权限中有不同意授予的
            mRequestBackCall.requestFail(deniedPermissions);
        }
    }
}
