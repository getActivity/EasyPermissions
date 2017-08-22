package com.hjq.md.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.hjq.md.permission.annotations.NoPermission;
import com.hjq.md.permission.annotations.HasPermission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJQ on 2017-5-9.
 */

public class PermissionUtils {

    //不能被实例化
    private PermissionUtils() {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + "不能被实例化");
    }

    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 执行成功的方法
     * @param object                Activity或Fragment对象
     * @param requestCode           本次申请权限的请求码
     * @param succeedPermissions    请求成功的权限组
     */
    public static void executeSucceesMethod(Object object, int requestCode, String[] succeedPermissions) {

        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods){
            //System.out.println(method);
            //获取该方法上面有没有打这个注解
            HasPermission succeedMethod = method.getAnnotation(HasPermission.class);
            if (succeedMethod != null){
                int methodCode = succeedMethod.value();
                //并且我们的请求码必须要一致
                if (methodCode == requestCode){

                    method.setAccessible(true);//允许执行私有方法
                    try {

                        object.getClass().getDeclaredMethod(method.getName(), String[].class);
                        //调用时直接传一个String[]实例会报异常，需要再次用Object[]包装一下
                        method.invoke(object, new Object[]{succeedPermissions});

                    } catch (NoSuchMethodException e) {
                        //这个方法没有参数
                        try {
                            object.getClass().getDeclaredMethod(method.getName());
                            method.invoke(object, new Object[]{});
                        } catch (NoSuchMethodException e1) {
                            throw new IllegalArgumentException("请不要在" +  object.getClass().getSimpleName() + "类中的" + method.getName() + "方法上添加不支持的参数");
                        } catch (IllegalAccessException e1) {
                        } catch (InvocationTargetException e1) {}

                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {}
                }
            }
        }
    }


    /**
     * 执行失败的方法
     * @param object            Activity或Fragment对象
     * @param requestCode       本次申请权限的请求码
     * @param failPermissions   请求失败的权限组
     */
    public static void executeFailMethod(Object object, int requestCode, String[] failPermissions) {
        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods){
            //System.out.println(method);
            //获取该方法上面有没有打这个注解
            NoPermission failMethod = method.getAnnotation(NoPermission.class);
            if (failMethod != null){
                int methodCode = failMethod.value();
                //并且我们的请求码必须要一致
                if (methodCode == requestCode){

                    method.setAccessible(true);//允许执行私有方法
                    try {
                        object.getClass().getDeclaredMethod(method.getName(), String[].class);
                        //调用时直接传一个String[]实例会报异常，需要再次用Object[]包装一下
                        method.invoke(object, new Object[]{failPermissions});

                    } catch (NoSuchMethodException e) {
                        //这个方法没有参数
                        try {
                            object.getClass().getDeclaredMethod(method.getName());
                            method.invoke(object, new Object[]{});
                        } catch (NoSuchMethodException e1) {
                            throw new IllegalArgumentException("请不要在" +  object.getClass().getSimpleName() + "类中的" + method.getName() + "方法上添加不支持的参数");
                        } catch (IllegalAccessException e1) {
                        } catch (InvocationTargetException e1) {}

                    } catch (IllegalAccessException e) {
                    } catch (InvocationTargetException e) {}
                }
            }
        }
    }

    /**
     * 获取没有授予的权限
     * @param object                Activity或Fragment对象
     * @param requestPermissions    需要请求的权限组
     */
    public static String[] getFailPermissions(Object object, String[] requestPermissions) {
        List<String> failPermissions = new ArrayList<>();
        for (String permission : requestPermissions){
            //把没有授予过的权限加入到集合中
            if (ContextCompat.checkSelfPermission(getContext(object), permission) == PackageManager.PERMISSION_DENIED){
                failPermissions.add(permission);
            }
        }
        return failPermissions.toArray(new String[failPermissions.size()]);
    }

    /**
     * 获取没有授予的权限
     * @param requestPermissions    需要请求的权限组
     * @param grantResults          允许结果组
     */
    public static String[] getFailPermissions(String[] requestPermissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++){

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == -1){
                failPermissions.add(requestPermissions[i]);
            }
        }
        return failPermissions.toArray(new String[failPermissions.size()]);
    }

    /**
     * 获取Object的Activity对象
     * @param object        Activity或Fragment对象
     */
    @SuppressLint("NewApi")
    public static Activity getContext(Object object) {

        if (object instanceof Activity){
            return (Activity) object;
        }else if (object instanceof android.support.v4.app.Fragment){
            return ((android.support.v4.app.Fragment)object).getActivity();
        }else if(object instanceof Fragment){
            return ((Fragment)object).getActivity();
        }
        return null;
    }

    /**
     * 申请权限
     * @param object            Activity或Fragment对象
     * @param permissions       需要请求的权限组
     * @param requestCode       本次申请权限的请求码
     */
    @SuppressLint("NewApi")
    public static void requestPermissions(Object object, String[] permissions, int requestCode) {
        if (object instanceof Activity){
            ActivityCompat.requestPermissions((Activity) object, permissions, requestCode);
        }else if (object instanceof android.support.v4.app.Fragment){
            ((android.support.v4.app.Fragment) object).requestPermissions(permissions, requestCode);
        }else if(object instanceof Fragment){
            ((Fragment) object).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 检测对象是否符合要求
     * @param object        Activity或Fragment对象
     */
    public static void checkObject(Object object) {

        if (object == null) {
            throw new NullPointerException("必须传入Activity或Fragment");
        }else if (!PermissionUtils.isCorrectObject(object)){
            throw new IllegalArgumentException(object.getClass().getSimpleName() + "这个对象不是Activity或Fragment");
        }
    }

    /**
     * 检查对象是否是Activity或Fragment
     * @param object        Activity或Fragment对象
     */
    public static boolean isCorrectObject(Object object) {
        if (object instanceof  Activity || object instanceof android.support.v4.app.Fragment || object instanceof Fragment){
            return  true;
        }
        return false;
    }

    /**
     * 检查权限是否为空
     * @param permissions       需要检查的权限组
     */
    public static void isEmptyPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("需要请求的权限必须指定一个及以上");
        }
    }
}
