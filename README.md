# 权限请求框架

> 这是一个Module工程，使用前请先导入

> 有两种方式请求权限，推荐使用第一种方式

> 支持以下类中进行的请求权限操作

* android.app.Activity

* android.app.Fragment

* android.support.v4.app.Fragment

## 第一种方式（回调接口）

> 在Activity或Fragment下请求权限示例，可直接复制粘贴

    //动态申请文件读写权限
    public void requestFilePermissions() {

        //判断权限是否已经获取
        if (EasyPermission.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(mContext, "已经获取到SD卡读写权限，不需要再次申请了", Toast.LENGTH_SHORT).show();
            return;
        }

        //动态申请SD卡读写权限
        EasyPermission.requestPermissions(this, new EasyPermission.OnRequestCallBack() {
            
            @Override
            public void requestSucceed(String[] succeedPermissions) {
                Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFail(String[] failPermissions) {
                Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
                //跳转到应用权限设置页面
                EasyPermission.gotoPermissionSettings(mContext);
            }

        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //多个权限请求只需要调用一次
        EasyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

## 第二种方式（注解方法）

> 在Activity或Fragment下请求权限示例，可直接复制粘贴

    private static final int requestCode = 100;//文件读取权限请求码

    //动态申请文件读写权限
    public void requestFilePermissions() {

        //判断权限是否已经获取
        if (SimplePermission.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(mContext, "已经获取到SD卡读写权限，不需要再次申请了", Toast.LENGTH_SHORT).show();
            return;
        }

        //动态申请SD卡读写权限
        SimplePermission.requestPermissions(this, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //覆盖Activity或Fragment中的方法，可将此方法封装到BaseActivity或者BaseFragment中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //多个权限请求只需要调用一次
        SimplePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @HasPermission(requestCode)
    public void openFileSucceed() {
        Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
    }

    @NoPermission(requestCode)
    public void openFileFail() {
        Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
        //跳转到应用权限设置页面
        EasyPermission.gotoPermissionSettings(mContext);
    }

    //如果需要方法参数可以使用这两个方法，不能和无参的方法一起使用
    //@HasPermission(requestCode)
    //public void openFileSucceed(String[] succeedPermissions) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限成功，有" + succeedPermissions.length + "个权限请求成功", Toast.LENGTH_SHORT).show();
    //}
	//
    //@NoPermission(requestCode)
    //public void openFileFail(String[] failPermissions) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限失败，有" + failPermissions.length + "个权限请求失败", Toast.LENGTH_SHORT).show();
    //}

#### 权限基类

> 可以让你的Activity或者BaseActivity继承PermissionActivity，这样子类Activity及关联的Fragment可以不用重写onRequestPermissionsResult方法，也无需调用权限处理类中的方法

> 下面是PermissionActivity的源码，仅供参考

    public class PermissionActivity extends AppCompatActivity {

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
            SimplePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

#### 跳转到应用权限系统设置页面

> EasyPermission

    EasyPermission.gotoPermissionSettings(mContext);

> SimplePermission

    EasyPermission.gotoPermissionSettings(mContext);