package com.hjq.md.Permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJQ on 2017-5-9.
 */

public class PermissionUtils {

    //不能被实例化
    private PermissionUtils(){
        throw new UnsupportedOperationException(this.getClass().getName() + "不能被实例化");
    }

    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 执行成功的方法
     */
    public static void executeSucceesMethod(Object object, int requestCode) {

        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods){
            //System.out.println(method);
            //获取该方法上面有没有打这个注解
            PermissionSucceed succeedMethod = method.getAnnotation(PermissionSucceed.class);
            if (succeedMethod != null){
                int methodCode = succeedMethod.value();
                //并且我们的请求码必须要一致
                if (methodCode == requestCode){
                    //反射执行该方法
                    executeMethod(object, method);
                }
            }
        }
    }

    /**
     * 执行失败的方法
     */
    public static void executeFailMethod(Object object, int requestCode, String[] deniedPermissions) {
        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods){
            //System.out.println(method);
            //获取该方法上面有没有打这个注解
            PermissionFail failMethod = method.getAnnotation(PermissionFail.class);
            if (failMethod != null){
                int methodCode = failMethod.value();
                //并且我们的请求码必须要一致
                if (methodCode == requestCode){

                        method.setAccessible(true);//允许执行私有方法
                        try {
                            object.getClass().getDeclaredMethod(method.getName(), String[].class);
                            //调用时直接传一个String[]实例会报异常，需要再次用Object[]包装一下
                            method.invoke(object, new Object[]{deniedPermissions});

                        } catch (NoSuchMethodException e) {
                            //这个方法没有参数
                            try {
                                method.invoke(object, new Object[]{});
                            } catch (IllegalAccessException e1) {
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                e1.printStackTrace();
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                }
            }
        }
    }


    /**
     * 反射执行某个方法
     */
    private static void executeMethod(Object object, Method method) {
        try {
            method.setAccessible(true);//允许执行私有方法
            method.invoke(object, new Object[]{});//这个方法没有参数
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取没有授予的权限
     */
    public static String[] getDeniedPermissions(Object object, String[] requestPermissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission: requestPermissions){
            //把没有授予过的权限加入到集合中
            if (ContextCompat.checkSelfPermission(getContext(object), permission) == PackageManager.PERMISSION_DENIED){
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 获取Object的上下文对象
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
     */
    @SuppressLint("NewApi")
    public static void requestPermissions(Object object, String[] permissions, int requestCode){
        if (object instanceof Activity){
            ActivityCompat.requestPermissions((Activity) object, permissions, requestCode);
        }else if (object instanceof android.support.v4.app.Fragment){
            ((android.support.v4.app.Fragment)object).requestPermissions(permissions, requestCode);
        }else if(object instanceof Fragment){
            ((Fragment)object).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 判断当前对象是否是Activity或Fragment
     */
    public static boolean isCorrectObject(Object object){
        if (object instanceof  Activity || object instanceof Fragment || object instanceof android.support.v4.app.Fragment){
            return  true;
        }
        return false;
    }

    /**
     * 对比两个字符串数组是否相同
     */
    public static boolean equalStringArray(String[] s1, String[] s2) {
        if (s1.length == s2.length) {
            for (int i = 0; i < s1.length; i++) {
                if (!s1[i].equals(s2[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
