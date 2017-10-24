package com.hjq.permission;

import android.content.Context;

/**
 * Created by HJQ on 2017-10-5.
 */

class BasePermission {

    /**
     * 跳转到应用权限设置页面
     * @param context           上下文对象
     */
    public static void gotoPermissionSettings(Context context) {
        PermissionUtils.gotoPermissionSettings(context);
    }

    /**
     * 检查某些权限是否全部授予了
     * @param context           上下文对象
     * @param permissions       需要请求的权限组
     * @return
     */
    public static boolean checkSelfPermission(Context context, String... permissions) {
        return PermissionUtils.getFailPermissions(context, permissions).length == 0;
    }
}
