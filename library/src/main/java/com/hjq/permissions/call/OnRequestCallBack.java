package com.hjq.permissions.call;

import com.hjq.permissions.bean.PermissionInfo;

import java.util.List;

/**
 * Created by HJQ on 2017-10-5.
 *
 * 权限请求回调接口
 */

public interface OnRequestCallBack {

    /**
     * 有权限被授予时回调
     *
     * @param granted        请求成功的权限组
     */
    void hasPermission(List<PermissionInfo> granted);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied            请求失败的权限组
     * @param quick             是否为系统自动拒绝的
     */
    void noPermission(List<PermissionInfo> denied, boolean quick);
}
