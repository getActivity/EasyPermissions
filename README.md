# 权限请求框架

>直接将permission文件夹复制到项目包下即可

>支持以下类或子类中进行的请求权限操作

* android.app.Activity

* android.app.Fragment

* android.support.v4.app.Fragment

## 第一种方式（推荐）

#### 通用代码（在Activity或Fragment下请求权限示例，可直接复制）

    //打开文件
    public void openFile(){
		
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//覆盖Activity或Fragment中的方法，多个请求只需要调用一次
        EasyPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

## 第二种方式

#### 通用代码（在Activity或Fragment下请求权限示例，可直接复制）

    private static final int requestCode = 100;//权限请求码

    //打开文件
    public void openFile(){

        SimplePermission.requestPermissions(this, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		//覆盖Activity或Fragment中的方法，多个请求只需要调用一次
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
