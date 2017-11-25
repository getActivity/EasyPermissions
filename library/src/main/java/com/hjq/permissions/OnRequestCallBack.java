package com.hjq.permissions;

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
    void hasPermission(List<String> granted);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied            请求失败的权限组
     * @param permanent         是否永久拒绝了此权限
     */
    void noPermission(List<String> denied, boolean permanent);
}
