package activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import util.ConstantValue;
import util.SpUtils;
import util.StreamUtil;
import util.ToastUtil;


public class SplashActivity extends AppCompatActivity {
    /**
     * 更新新版本的状态码
     */
    private static final int UPDATE_VERSION = 100;
    /**
     * 进入主界面状态码
     */
    private static final int ENTER_HOME = 101 ;
    /**
     * 异常状态码
     */
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;

    private final String TAG="Life_SplashActivity";
    private TextView tv_version_name;
    private RelativeLayout rl_root;
    private String mVersionName;
    private int mLovalVersionCode;//本地版本号
    private  String mVersionDes;
    private  String mDownloadUrl;
    private int mVersionCode;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //ctrl+alt+下键，向下拷贝当前代码
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_VERSION:
                    //弹出对话框
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面，Activity跳转,报异常不影响用户使用
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(),"io异常");
                    enterHome();
                    break;
                case URL_ERROR:
                    //常见操作封装到工具类
//                    Toast.makeText(SplashActivity.this,"网络故障",Toast.LENGTH_SHORT).show();
                    ToastUtil.show(getApplicationContext(),"url异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(),"json异常");
                    enterHome();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"启动页面成功");
        initUI();
//      初始化数据
        initData();
//      初始化动画
        initAnimation();
    }

    /**
     * 初始化动画，淡入
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);
    }

    /**
     * 弹出对话框，提示用户更新
     */
    private void showUpdateDialog() {
        //对话框依赖于activity存在的
        final AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle("update");
        builer.setIcon(R.drawable.icon);
        builer.setMessage(mVersionDes);
        builer.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk
                downloadApk();
            }
        });
        builer.setNegativeButton("skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //取消对话框，进入主界面
                enterHome();
            }
        });
        builer.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消，也需要让其进入应用程序主界面
                enterHome();
                dialog.dismiss();
            }
        });
        builer.show();


    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        //apk先下载的连接地址downloadUrl，放置apk所在的路径
        //1.判断sdk是否可以

        String path;
       /* if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //2.获取sdk路径
            path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    File.separator + "KMSafe20.apk";
        }else {*/
//            File apkfile = new File("KMSafe21.apk");
            File apkfile = getDir("KMSafe21.apk", Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
            path=apkfile.getAbsolutePath();
//        }
        Log.i(TAG,"path:"+path);
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){

            //3.发出请求，获取apk，并且放到指定路径
            HttpUtils httpUtils = new HttpUtils();
            //4.发送请求，传递参数（下载地址，应用放置地址）
        mDownloadUrl="http://119.29.62.167/KMSafe/version.json";
                httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功（下载成功放在sdk卡中的apk）
                    Log.i(TAG,"下载成功");

                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.i(TAG,"下载失败:"+s);

                }
                //参数说明：下载总长度，当前下载位置，是否正在下载
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //下载过程
                    Log.i(TAG,"正在下载");
                }
                });
        }
//    }

    /**
     * 安装下载的apk
     * @param file
     */
    private void installApk(File file) {
        //系统应用界面，源码接口，安装apk入口,隐式意图,匹配系统activity的过滤器
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源同时设置类型
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivityForResult(intent,0);
    }

    /**
     * 开启一个Activity后，返回结果调用的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入应用程序的主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭（导航界面只可见一次）
        finish();
    }




    /**
     * 初始化UI alt+shift+j
     */
    private void initUI() {
        //ctrl+1
        tv_version_name=(TextView) findViewById(R.id.version_name);
        rl_root= (RelativeLayout) findViewById(R.id.root);
    }

    /**
     * 获取数据方法
     */
    private void initData() {
        //应用版本名称
        tv_version_name.setText("版本名称："+getVersionName());
        Log.i(TAG,"getVersionName 版本名称"+tv_version_name.getText().toString());
        //获取本地版本号
        mLovalVersionCode=getVersionCode();
        Log.i(TAG,"getVersionCode 版本号"+mLovalVersionCode);
        //检测（本地版本号和服务器版本号进行对比）是否有更新，如果有更新，提示用户下载（member）
        //获取服务器版本号（客户端发起请求，服务端给响应，（json,xml））
        //http://119.29.62.167?key=value 返回200请求成功，通过流的方式读取数据
        /**
         * json中应该包含
         *      最新版本名称
         *      新版本描述信息
         *      服务器版本号
         *      新版本apk下载地址
         */
        if(SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE,false)){
            checkVersion();
        }else {
            //在发送消息4s后去处理ENTER_HOME状态码
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,4000);
        }



    }

    /**
     * 检查版本号
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"启动线程");
                long startTime = System.currentTimeMillis();
                //发送请求获取数据，参数则为请求json的链接地址
                Message msg = Message.obtain();
                try{
                    //封装url地址
                    URL url =new URL("http://119.29.62.167/KMSafe/version.json");
                    //开启一个连接http://119.29.62.167/KMSafe/version.json，默认端口号是80
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    //设置常见请求参数
                    //请求超时
                    connection.setConnectTimeout(8000);
                    //读取超时
                    connection.setReadTimeout(8000);
                    //默认为get方法
//                    Log.i(TAG,"connection.getResponseCode()"+connection.getResponseCode());
                    if (connection.getResponseCode()==200){//io异常
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.streamToString(is);
                        Log.i(TAG,json);
                        JSONObject jsonObject =  new JSONObject(json);
                        mVersionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        mVersionCode = Integer.parseInt(jsonObject.getString("versionCode"));

                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        //debug调式优化
                        Log.i(TAG,"versionName:"+mVersionName);
                        Log.i(TAG,"versionDes:"+ mVersionDes);
                        Log.i(TAG,"versionCode:"+mVersionCode+"");
                        Log.i(TAG,"downloadUrl:"+ mDownloadUrl);
                        Log.i(TAG,"mLovalVersionCode<versionCode:"+(mLovalVersionCode<mVersionCode));
                    //比对版本号
                        if (mLovalVersionCode<mVersionCode){
                            //提示用户更新，弹出对话框（UI）主线程不能处理UI
                            msg.what = UPDATE_VERSION;
                        }else{
                            //进入程序主界面
                            msg.what = ENTER_HOME;
                        }
                    }
                }catch (MalformedURLException e){
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                }catch (IOException e){
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                }catch (JSONException e){
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                }finally {
                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime<4000){
                        try {
                            Thread.sleep(4000-(endTime-startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //指定睡眠时间，请求网络时长超过4s则不处理
                    //请求网络时长不小于4s，强制让其睡眠4s
                    Log.i(TAG,"msg.what:"+msg.what);
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 返回服务器版本号
     * @return
     * 非0则代表获取成功
     */
    private int  getVersionCode() {
        //包管理者对象
        PackageManager pm = getPackageManager();
        //从包管理者对象中，获取版本信息（版本名称，版本号）
        try {
            PackageInfo packageinfo =  pm.getPackageInfo(SplashActivity.this.getPackageName(),0);
            return packageinfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;

    }

    /**
     * 获取版本名称：清单文件中获取本地的版本名称
     * @return 应用版本名称
     * null:异常
     */
    public String getVersionName(){
        //包管理者对象
        PackageManager pm = getPackageManager();
        //从包管理者对象中，获取版本信息（版本名称）
        try {
            PackageInfo packageinfo =  pm.getPackageInfo(SplashActivity.this.getPackageName(),0);
            return packageinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
