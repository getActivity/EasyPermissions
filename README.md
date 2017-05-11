# 权限请求框架

>直接将Permission文件夹复制到项目包下即可

>支持以下类或子类中进行的请求权限操作

* android.app.Activity

* android.app.Fragment

* android.support.v4.app.Fragment

## 第一种方式（推荐）

#### 通用代码（在Activity或Fragment下请求权限，可直接复制）

    private EasyPermission easyPermission;
    private final String[] requestPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //打开文件
    public void openFile(){
		
        easyPermission = new EasyPermission(this, requestPermission, new EasyRequestBackCall() {
            @Override
            public void requestSucceed() {
                System.out.println("获取SD卡读取写入权限成功");
            }

            @Override
            public void requestFail(String[] deniedPermissions) {
                System.out.println("获取SD卡读取写入权限失败");
            }
        });
    }

	//覆盖Activity或Fragment中的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (easyPermission != null) {
            easyPermission.onRequestPermissionsResult(this, requestCode, permissions);
        }
    }


## 第二种方式

#### 通用代码（在Activity或Fragment下请求权限，可直接复制）

	private SimplePermission simplePermission;
    private final int requestCode = 100;
    private final String[] requestPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //打开文件
    public void openFile(){

        simplePermission = new SimplePermission(this, requestCode, requestPermission);
    }

	//覆盖Activity或Fragment中的方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (simplePermission != null) {
			simplePermission.onRequestPermissionsResult(this, requestCode, permissions);
		}
    }

    @PermissionSucceed(requestCode)
    public void openFileSucceed(){
        System.out.println("获取SD卡读取写入权限成功");
    }

    @PermissionFail(requestCode)
    public void openFileFail(){
        System.out.println("获取SD卡读取写入权限失败");
    }

