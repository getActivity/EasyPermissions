package com.hjq.md.permission;

import java.util.HashMap;

/**
 * Created by HJQ on 2017-5-9.
 */

public class SimplePermission {

    private static HashMap<Integer, Object> mHashMap = new HashMap();

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     * @param object            Activity或Fragment对象
     * @param requestCode       本次申请权限的请求码
     * @param permissions       需要请求的权限组
     */
    public static void requestPermissions(Object object, int requestCode, String... permissions){

        PermissionUtils.checkObject(object);

        PermissionUtils.isEmptyPermissions(permissions);

        if (permissions.length == 0) {
            throw new IllegalArgumentException("需要请求的权限必须指定一个及以上");
        }

        //如果版本不是6.0及以上,通过注解的形式，反射执行方法
        if(!(PermissionUtils.isOverMarshmallow())) {
            PermissionUtils.executeSucceesMethod(object, requestCode, permissions);
            return;
        }

        String[] failPermissions = PermissionUtils.getFailPermissions(object, permissions);

        if (failPermissions.length == 0){
            //证明权限已经全部授予过
            PermissionUtils.executeSucceesMethod(object, requestCode, permissions);
        }else{
            //将当前的请求码和对象添加到集合中
            mHashMap.put(requestCode, object);
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(object, failPermissions, requestCode);
        }
    }

    /**
     * 在Activity或Fragment中的同名方法调用此方法
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Object object = mHashMap.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (object == null){
            return;
        }

        //再次获取没有授予的权限
        String[] failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);
        if (failPermissions.length == 0){
            //代表申请的所有的权限都授予了
            PermissionUtils.executeSucceesMethod(object, requestCode, permissions);
        }else{
            //代表申请的权限中有不同意授予的
            PermissionUtils.executeFailMethod(object, requestCode, failPermissions);
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        mHashMap.remove(requestCode);
    }
}
