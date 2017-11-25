package com.hjq.permissions;

import android.app.Activity;
import android.app.Fragment;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by HJQ on 2017-5-9.
 *
 * 通过回调接口请求权限
 */

public final class EasyPermissions {

    /**
     * 不能被实例化
     */
    private EasyPermissions() {}

    /**
     * 请求权限，不需要指定请求码，请求结果通过回调接口方式实现
     *
     * @param activity          Activity对象
     * @param call              用于接收结果的回调
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Activity activity, OnRequestCallBack call, String... permissions) {
        request((Object) activity, call, permissions);
    }

    /**
     * 请求权限，不需要指定请求码，请求结果通过回调接口方式实现
     *
     * @param fragment          Fragment对象
     * @param call              用于接收结果的回调
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Fragment fragment, OnRequestCallBack call, String... permissions) {
        request((Object) fragment, call, permissions);
    }

    /**
     * 请求权限，不需要指定请求码，请求结果通过回调接口方式实现
     *
     * @param fragment          v4包下的Fragment对象
     * @param call              用于接收结果的回调
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(android.support.v4.app.Fragment fragment, OnRequestCallBack call, String... permissions) {
        request((Object) fragment, call, permissions);
    }

    private final static SparseArray<OnRequestCallBack> mContainer = new SparseArray<>();

    private static long requestTime;

    private static void request(Object object, OnRequestCallBack call, String... permissions) {

        PermissionUtils.checkObject(object);

        PermissionUtils.isEmptyPermissions(permissions);

        if (call == null) throw new NullPointerException("权限请求回调接口必须要实现");

        int requestCode;

        //请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            //requestCode = new Random().nextInt(65535);//Studio编译的APK请求码必须小于65536
            requestCode = new Random().nextInt(255);//Eclipse编译的APK请求码必须小于256
        } while (mContainer.get(requestCode) != null);

        List<String> failPermissions = PermissionUtils.getFailPermissions(PermissionUtils.getContext(object), permissions);

        if (failPermissions.isEmpty()) {
            //证明权限已经全部授予过
            call.hasPermission(Arrays.asList(permissions));
        } else {
            //将当前的请求码和对象添加到集合中
            mContainer.put(requestCode, call);
            //记录本次申请时间
            requestTime = System.currentTimeMillis();
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(object, failPermissions, requestCode);
        }
    }

    /**
     * 在Activity或Fragment中的同名同参方法调用此方法
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        OnRequestCallBack call = mContainer.get(requestCode);

        //根据请求码取出的对象为空，就直接返回不处理
        if (call == null) return;

        List<String> succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        List<String> failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);
        //如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (succeedPermissions.size() == permissions.length) {
            //代表申请的所有的权限都授予了
            call.hasPermission(succeedPermissions);
        }else {
            //代表申请的权限中有不同意授予的，如果拒绝的时间过快证明是系统自动拒绝
            call.noPermission(failPermissions, System.currentTimeMillis() - requestTime < 200);
            //证明还有一部分权限被成功授予，回调成功接口
            if (!succeedPermissions.isEmpty()) {
                call.hasPermission(succeedPermissions);
            }
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        mContainer.remove(requestCode);
    }
}
