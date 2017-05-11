package com.hjq.md.Permission;

/**
 * Created by HJQ on 2017-5-10.
 */

public interface EasyRequestBackCall {
    /**
     * 权限请求成功时回调
     */
    void requestSucceed();

    /**
     * 权限请求失败时回调
     * @param deniedPermissions   被拒绝的权限组
     */
    void requestFail(String[] deniedPermissions);
}
