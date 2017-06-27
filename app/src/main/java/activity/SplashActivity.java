package activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.julse.jules.kmsafe.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import util.StreamUtil;


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

    private final String TAG="Life";
    private TextView tv_version_name;
    private int mLovalVersionCode;//本地版本号
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //ctrl+alt+下键，向下拷贝当前代码
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_VERSION:
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面，Activity跳转
                    enterHome();
                    break;
                case URL_ERROR:
                    break;
                case JSON_ERROR:
                    break;
            }
        }
    };

    /**
     * 进入应用程序的主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭（导航界面只可见一次）
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"启动页面成功");
        initUI();
//      初始化数据
        initData();
    }

    /**
     * 初始化UI alt+shift+j
     */
    private void initUI() {
        //ctrl+1
        tv_version_name=(TextView) findViewById(R.id.version_name);

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
        checkVersion();


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
                    Log.i(TAG,"connection.getResponseCode()"+connection.getResponseCode());
                    if (connection.getResponseCode()==200){
                        InputStream is = connection.getInputStream();
                        String json = StreamUtil.streamToString(is);
                        Log.i(TAG,json);
                        JSONObject jsonObject =  new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        String versionDes = jsonObject.getString("versionDes");
                        int versionCode = Integer.parseInt(jsonObject.getString("versionCode"));
                        String downloadUrl = jsonObject.getString("downloadUrl");
                        //debug调式优化
                        Log.i(TAG,"versionName:"+versionName);
                        Log.i(TAG,"versionDes:"+versionDes);
                        Log.i(TAG,"versionCode:"+versionCode+"");
                        Log.i(TAG,"downloadUrl:"+downloadUrl);
                    //比对版本号
                        if (mLovalVersionCode>versionCode){
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
                    Log.i(TAG,"URL"+"异常");
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