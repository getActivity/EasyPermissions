# 权限请求框架

> 直接将permission文件夹复制到项目包下即可

> 支持以下类中进行的请求权限操作

* android.app.Activity

* android.app.Fragment

* android.support.v4.app.Fragment

## 第一种方式（回调接口）

> 在Activity或Fragment下请求权限示例，可直接复制

    //动态申请文件读写权限
    public void requestFilePermissions(){
		
        EasyPermission.requestPermissions(this, new EasyPermission.EasyRequestBackCall() {
            
            @Override
            public void requestSucceed(String[] succeedPermissions) {
                Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestFail(String[] failPermissions) {
                Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
            }

        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    //覆盖Activity或Fragment中的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //多个权限请求只需要调用一次
        EasyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

## 第二种方式（注解方法）

> 在Activity或Fragment下请求权限示例，可直接复制

    private static final int requestCode = 100;//文件读取权限请求码

    //动态申请文件读写权限
    public void requestFilePermissions(){

        SimplePermission.requestPermissions(this, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    //覆盖Activity或Fragment中的方法
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
    }

    //@HasPermission(requestCode)
    //public void openFileSucceed(String[] succeedPermissions) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限成功", Toast.LENGTH_SHORT).show();
    //}
	//
    //@NoPermission(requestCode)
    //public void openFileFail(String[] failPermissions) {
    //    Toast.makeText(mContext, "获取SD卡读取写入权限失败", Toast.LENGTH_SHORT).show();
    //}
