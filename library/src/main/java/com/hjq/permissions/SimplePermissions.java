package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by HJQ on 2017-5-9.
 *
 * 通过注解方法请求权限
 */

public final class SimplePermissions {

    /**
     * 不能被实例化
     */
    private SimplePermissions() {}

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     *
     * @param activity          Activity对象
     * @param requestCode       本次申请权限的请求码
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Activity activity, int requestCode, String... permissions) {
        request((Object) activity, requestCode, permissions);
    }

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     *
     * @param fragment          Fragment对象
     * @param requestCode       本次申请权限的请求码
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Fragment fragment, int requestCode, String... permissions) {
        request((Object) fragment, requestCode, permissions);
    }

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     *
     * @param fragment          v4包下的Fragment对象
     * @param requestCode       本次申请权限的请求码
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(android.support.v4.app.Fragment fragment, int requestCode, String... permissions) {
        request((Object) fragment, requestCode, permissions);
    }

    private final static SparseArray mContainer = new SparseArray();

    private static void request(Object object, int requestCode, String... permissions) {

        PermissionUtils.checkObject(object);

        PermissionUtils.isEmptyPermissions(permissions);

        if (requestCode > 65535) throw new IllegalArgumentException(requestCode + "请求码不能太大");

        List<String> failPermissions = PermissionUtils.getFailPermissions(PermissionUtils.getContext(object), permissions);

        if (failPermissions.isEmpty()) {
            //证明权限已经全部授予过
            PermissionUtils.executeSucceedMethod(object, requestCode, Arrays.asList(permissions));
        } else {
            //将当前的请求码和对象添加到集合中
            mContainer.put(requestCode, object);
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(object, failPermissions, requestCode);
        }
    }

    /**
     * 在Activity或Fragment中的同名同参方法调用此方法
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Object object = mContainer.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (object == null) return;

        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);
        //如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            //代表申请的所有的权限都授予了
            PermissionUtils.executeSucceedMethod(object, requestCode, succeedPermissions);
        }else {
            //代表申请的权限中有不同意授予的
            PermissionUtils.executeFailMethod(object, requestCode, failPermissions);
            //证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                PermissionUtils.executeSucceedMethod(object, requestCode, succeedPermissions);
            }
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        mContainer.remove(requestCode);
    }
}
