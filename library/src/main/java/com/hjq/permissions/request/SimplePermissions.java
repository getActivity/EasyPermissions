package com.hjq.permissions.request;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.util.SparseArray;

import com.hjq.permissions.bean.PermissionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Activity activity, String... permissions) {
        request((Object) activity, permissions);
    }

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     *
     * @param fragment          Fragment对象
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(Fragment fragment, String... permissions) {
        request((Object) fragment, permissions);
    }

    /**
     * 请求权限，需要指定请求码，请求结果通过调用Activity或者Fragment注解方法的方式实现
     *
     * @param fragment          v4包下的Fragment对象
     * @param permissions       需要请求的权限组，可变参数类型
     */
    public static void request(android.support.v4.app.Fragment fragment, String... permissions) {
        request((Object) fragment, permissions);
    }

    private final static SparseArray mContainer = new SparseArray();

    private static long requestTime;

    private static void request(Object object, String... permissions) {

        PermissionUtils.checkObject(object);

        PermissionUtils.isEmptyPermissions(permissions);

        int requestCode;

        //请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
        do {
            //requestCode = new Random().nextInt(65535);//Studio编译的APK请求码必须小于65536
            requestCode = new Random().nextInt(255);//Eclipse编译的APK请求码必须小于256
        } while (mContainer.get(requestCode) != null);


        if (PermissionUtils.getFailPermissions(PermissionUtils.getActivity(object), permissions).length == 0) {
            //证明权限已经全部授予过
            PermissionUtils.executeSucceedMethod(object, PermissionUtils.arrayConversion(permissions, true, false, false));
        } else {
            //将当前的请求码和对象添加到集合中
            mContainer.put(requestCode, object);
            //记录本次请求时间
            requestTime = System.currentTimeMillis();
            //申请没有授予过的权限
            PermissionUtils.requestPermissions(object, permissions, requestCode);
        }
    }

    /**
     * 在Activity或Fragment中的同名同参方法调用此方法
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Object object = mContainer.get(requestCode);
        //根据请求码取出的对象为空，就直接返回不处理
        if (object == null) return;

        String[] succeedPermissions = PermissionUtils.getSucceedPermissions(permissions, grantResults);
        String[] failPermissions = PermissionUtils.getFailPermissions(permissions, grantResults);

        //证明有部分或者全部权限被成功授予
        if (succeedPermissions.length != 0) {
            PermissionUtils.executeSucceedMethod(object, PermissionUtils.arrayConversion(succeedPermissions, true, false, false));
        }

        //证明有部分或者全部权限被拒绝授予
        if (failPermissions.length != 0) {
            //代表申请的权限中有不同意授予的
            List<PermissionInfo> infos = new ArrayList<>();
            for (String permission : failPermissions) {
                infos.add(new PermissionInfo().setName(permission).setGranted(false)
                        .setPermanent(PermissionUtils.checkPermissionPermanentDenied(PermissionUtils.getActivity(object), permission))
                        .setExplain(ActivityCompat.shouldShowRequestPermissionRationale(PermissionUtils.getActivity(object), permission)));
            }
            //如果拒绝的时间过快证明是系统自动拒绝
            PermissionUtils.executeFailMethod(object, infos, System.currentTimeMillis() - requestTime < 200);
        }

        //权限回调结束后要删除集合中的对象，避免重复请求
        mContainer.remove(requestCode);
    }
}
