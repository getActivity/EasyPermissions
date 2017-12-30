package com.hjq.permissions.bean;

/**
 * Created by HJQ on 2017-12-16.
 *
 * 权限信息Bean类
 */

public class PermissionInfo {

    /**
     * 权限名称
     */
    private String name;

    /**
     * 是否请求成功
     */
    private boolean granted;

    /**
     * 是否被永久拒绝
     */
    private boolean permanent;

    /**
     * 是否需要自行向用户解释
     */
    private boolean explain;

    public String getName() {
        return name;
    }

    public PermissionInfo setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isGranted() {
        return granted;
    }

    public PermissionInfo setGranted(boolean granted) {
        this.granted = granted;
        return this;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public PermissionInfo setPermanent(boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    public boolean isExplain() {
        return explain;
    }

    public PermissionInfo setExplain(boolean explain) {
        this.explain = explain;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return name.equals(obj.toString());
    }

    @Override
    public String toString() {
        return name;
    }
}
