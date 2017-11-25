package com.hjq.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HJQ on 2017-5-9.
 * 有权限被授予注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    /**
     * 权限请求码
     */
    int value();
}
