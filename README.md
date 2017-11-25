# 权限请求框架

> 有两种不同的方式请求权限，推荐使用第一种方式

> 支持以下类中进行的请求权限操作

* android.app.Activity

* android.app.Fragment

* android.support.v4.app.Fragment

> 如果请求的权限组部分没有被授予，会同时调用成功和失败的方法，具体可通过方法的List参数获取到被授予或被拒绝授予的权限组

> 编译时需要使用 targetSdkVersion >= 23 的Android版本进行编译，框架经过半年的维护已经很完美，做足了各种测试，对内存占用也进行了优化，完全能胜任各种开发需求

> [点击下载演示Demo](https://raw.githubusercontent.com/getActivity/EasyPermissions/master/EasyPermissionsDemo.apk)

![](EasyPermissions.gif)

## 第一种方式（回调接口）

> 在Activity或Fragment下请求权限示例，可直接复制粘贴

    //请求的权限组
    private static final String[] requestPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //动态申请文件读写权限
    public void requestFilePermissions() {

        EasyPermissions.request(this, new OnRequestCallBack() {
            
            @Override
            public void hasPermission(List<String> granted) {
                Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noPermission(List<String> denied, boolean permanent) {
                if(permanent) {
                    Toast.makeText(mContext, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                    PermissionUtils.gotoPermissionSettings(mContext);
                }else {
                    Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
                }
            }

        }, requestPermission);
    }

    //必须覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调权限请求框架，多个权限请求只需要调用一次
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

## 第二种方式（注解方法）

> 在Activity或Fragment下请求权限示例，可直接复制粘贴

	//本次权限请求码
    private static final int requestCode = 100;

    //请求的权限组
    private static final String[] requestPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //动态申请文件读写权限
    public void requestFilePermissions() {

        SimplePermissions.request(this, requestCode, requestPermission);
    }

    //必须覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调权限请求框架，多个权限请求只需要调用一次
        SimplePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @HasPermission(requestCode)
    public void openFileSucceed() {
        Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @NoPermission(requestCode)
    public void openFileFail() {
        Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
    }

    //如果需要参数可以使用这两个方法，不能和上面的两个无参方法一起使用
    //@HasPermission(requestCode)
    //public void openFileSucceed(List<String> granted) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限成功，有" + granted.length + "个权限请求成功", Toast.LENGTH_SHORT).show();
    //}
	//
    //@NoPermission(requestCode)
    //public void openFileFail(List<String> denied) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限失败，有" + denied.length + "个权限请求失败", Toast.LENGTH_SHORT).show();
    //}

#### 判断权限是否已经获取

    //请求的权限组
    String[] requestPermission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    if (PermissionUtils.isHasPermission(mContext, requestPermission)) {
        Toast.makeText(mContext, "已经获取到SD卡读写权限，不需要再次申请了", Toast.LENGTH_SHORT).show();
        return;
    }

#### 跳转到应用权限设置页面

    PermissionUtils.gotoPermissionSettings(mContext);

#### Activity权限基类

> 可以让你的Activity或者BaseActivity继承PermissionActivity，这样子类Activity及关联的Fragment可以不用重写onRequestPermissionsResult方法，也无需调用权限处理类中的方法

> 下面是PermissionActivity的源码，仅供参考

    public class PermissionActivity extends AppCompatActivity {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
            SimplePermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

#### 危险权限列表

> 同一组的任何一个权限被授权了，其他权限也自动被授权

![](DangerousPermissions.png)