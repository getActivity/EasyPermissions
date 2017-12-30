package com.hjq.permissions.request;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.hjq.permissions.annotations.HasPermission;
import com.hjq.permissions.annotations.NoPermission;
import com.hjq.permissions.bean.PermissionInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJQ on 2017-5-9.
 *
 * 请求权限工具类
 */

public final class PermissionUtils {

    /**
     * 不能被实例化
     */
    private PermissionUtils() {}

    /**
     * 跳转到应用权限设置页面
     *
     * @param context           上下文对象
     */
    public static void gotoPermissionSettings(Context context) {
        gotoPermissionSettings(context, false);
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context           上下文对象
     * @param newTask           是否使用新的任务栈启动
     */
    public static void gotoPermissionSettings(Context context, boolean newTask) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //创建一个新栈存放，用户在系统设置授予权限后，会导致返回后不会重新创建当前Activity，不推荐这种做法
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    /**
     * 检查某些权限是否全部授予了
     *
     * @param activity          Activity对象
     * @param permissions       需要请求的权限组
     */
    public static boolean isHasPermission(Activity activity, String... permissions) {
        return getFailPermissions(activity, permissions).length == 0;
    }

    /**
     * 当前是否为安卓6.0以上版本设备
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permission            请求的权限
     */
    public static boolean checkPermissionPermanentDenied(Activity activity, String permission) {
        if (PermissionUtils.isOverMarshmallow()) {
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
            }
        }
        return false;
    }

    /**
     * 执行成功的方法
     *
     * @param object                    Activity或Fragment对象
     * @param succeedPermissions        请求成功的权限组
     */
    static void executeSucceedMethod(Object object, List<PermissionInfo> succeedPermissions) {

        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods) {

            //获取该方法上面有没有打这个注解
            HasPermission succeedMethod = method.getAnnotation(HasPermission.class);
            if (succeedMethod != null) {

                method.setAccessible(true);//允许执行私有方法
                try {
                    object.getClass().getDeclaredMethod(method.getName(), List.class);
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

    /**
     * 执行失败的方法
     *
     * @param object                Activity或Fragment对象
     * @param failPermissions       请求失败的权限组
     * @param quick                 是否为系统自动拒绝的
     */
    static void executeFailMethod(Object object, List<PermissionInfo> failPermissions, boolean quick) {
        //获取class中所有的方法，然后逐个遍历
        Method[] methods  = object.getClass().getDeclaredMethods();
        //遍历找到我们打了标记的方法
        for (Method method : methods) {

            //获取该方法上面有没有打这个注解
            NoPermission failMethod = method.getAnnotation(NoPermission.class);
            if (failMethod != null) {

                method.setAccessible(true);//允许执行私有方法
                try {

                    //这个方法有两个参数
                    object.getClass().getDeclaredMethod(method.getName(), List.class, boolean.class);
                    //调用时直接传一个String[]实例会报异常，需要再次用Object[]包装一下
                    method.invoke(object, new Object[]{failPermissions, quick});

                } catch (NoSuchMethodException e) {

                    //这个方法只有一个参数
                    try {
                        object.getClass().getDeclaredMethod(method.getName(), List.class);
                        method.invoke(object, new Object[]{failPermissions});
                    } catch (NoSuchMethodException e1) {

                        //这个方法只有一个参数
                        try {
                            object.getClass().getDeclaredMethod(method.getName(), boolean.class);
                            method.invoke(object, new Object[]{quick});
                        } catch (NoSuchMethodException e2) {

                            //这个方法没有参数
                            try {
                                object.getClass().getDeclaredMethod(method.getName());
                                method.invoke(object, new Object[]{});
                            } catch (NoSuchMethodException e3) {
                                throw new IllegalArgumentException("请不要在" +  object.getClass().getSimpleName() + "类中的" + method.getName() + "方法上添加不支持的参数");
                            } catch (IllegalAccessException e3) {
                            } catch (InvocationTargetException e3) {}

                        } catch (IllegalAccessException e2) {
                        } catch (InvocationTargetException e2) {}

                    } catch (IllegalAccessException e1) {
                    } catch (InvocationTargetException e1) {}

                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {}
            }
        }
    }

    /**
     * 获取没有授予的权限
     *
     * @param activity               Activity对象
     * @param permissions            需要请求的权限组
     */
    static String[] getFailPermissions(Activity activity, String[] permissions) {

        //如果是安卓6.0以下版本就返回一个长度为零的数组
        if(!PermissionUtils.isOverMarshmallow()) {
            return new String[]{};
        }

        List<String> failPermissions = new ArrayList<>();
        for (String permission : permissions) {

            //把没有授予过的权限加入到集合中
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permission);
            }
        }
        return failPermissions.toArray(new String[]{});
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static String[] getSucceedPermissions(String[] permissions, int[] grantResults) {

        List<String> succeedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

            //把授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                succeedPermissions.add(permissions[i]);
            }
        }
        return succeedPermissions.toArray(new String[]{});
    }

    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    static String[] getFailPermissions(String[] permissions, int[] grantResults) {
        List<String> failPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length ; i++) {

            //把没有授予过的权限加入到集合中，-1表示没有授予，0表示已经授予
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                failPermissions.add(permissions[i]);
            }
        }
        return failPermissions.toArray(new String[]{});
    }

    /**
     * 获取Object的Activity对象
     *
     * @param object        Activity或Fragment对象
     */
    @SuppressLint("NewApi")
    static Activity getActivity(Object object) {

        if (object instanceof Activity) {
            return ((Activity) object);
        }else if (object instanceof android.support.v4.app.Fragment) {
            return ((android.support.v4.app.Fragment) object).getActivity();
        }else if(object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        }
        return null;
    }

    /**
     * 申请权限
     *
     * @param object            Activity或Fragment对象
     * @param permissions       需要请求的权限组
     * @param requestCode       本次申请权限的请求码
     */
    @SuppressLint("NewApi")
    static void requestPermissions(Object object, String[] permissions, int requestCode) {

        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, permissions, requestCode);
        }else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).requestPermissions(permissions, requestCode);
        }else if(object instanceof Fragment) {
            ((Fragment) object).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * 检测对象是否符合要求
     *
     * @param object        Activity或Fragment对象
     */
    static void checkObject(Object object) {

        if (object == null) {
            throw new NullPointerException("必须传入Activity或Fragment");
        }else if (!PermissionUtils.isCorrectObject(object)){
            throw new IllegalArgumentException(object.getClass().getSimpleName() + "这个对象不是Activity或Fragment");
        }
    }

    /**
     * 检查对象是否是Activity或Fragment
     *
     * @param object        Activity或Fragment对象
     */
    static boolean isCorrectObject(Object object) {
        if (object instanceof  Activity || object instanceof android.support.v4.app.Fragment || object instanceof Fragment){
            return  true;
        }else {
            return false;
        }
    }

    /**
     * 检查权限是否为空
     *
     * @param permissions       需要检查的权限组
     */
    static void isEmptyPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("需要请求的权限必须指定一个及以上");
        }
    }

    /**
     * 数组转换，将String数组转成PermissionInfo集合
     *
     * @param permissions           请求的权限组
     * @param granted               是否请求成功
     * @param permanent             是否被永久拒绝
     * @param explain               是否需要向用户解释
     */
    static List<PermissionInfo> arrayConversion (String[] permissions, boolean granted, boolean permanent, boolean explain) {
        ArrayList<PermissionInfo> infos = new ArrayList<>();
        for (String permission : permissions) {
            infos.add(new PermissionInfo().setName(permission).setGranted(granted).setPermanent(permanent).setExplain(explain));
        }
        return  infos;
    }
}
