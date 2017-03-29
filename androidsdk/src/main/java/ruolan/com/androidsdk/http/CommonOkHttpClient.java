package ruolan.com.androidsdk.http;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ruolan.com.androidsdk.http.https.HttpsUtils;
import ruolan.com.androidsdk.http.listener.DisposeDataHandle;
import ruolan.com.androidsdk.http.response.CommonJsonCallback;

/**
 * Created by wuyinlei on 2017/3/29.
 *
 * @function 请求的发送 请求参数配置 https支持
 */

public class CommonOkHttpClient {

    private static final int TIME_OUT = 30;  //超时操作
    private static OkHttpClient mOkHttpClient;

    //为我们的client配置参数
    static {

        //创建我们client对象的构建者
        OkHttpClient.Builder okHttpBuildeer = new OkHttpClient.Builder();

        //构建超时时间
        okHttpBuildeer.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuildeer.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        okHttpBuildeer.writeTimeout(TIME_OUT, TimeUnit.SECONDS);

        okHttpBuildeer.followRedirects(true);  //允许重定向

        //https支持
        okHttpBuildeer.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        okHttpBuildeer.sslSocketFactory(HttpsUtils.initSSLSocketFactory());

        //生成我们的client对象
        mOkHttpClient = okHttpBuildeer.build();
    }


    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }


    /**
     * 发送具体的http/https请求
     *
     * @param request        request
     * @param commenCallBack callback
     * @return Call
     */
    public static Call sendRequest(Request request, Callback commenCallBack) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(commenCallBack);
        return call;
    }

    /**
     * 通过构造好的Request,Callback去发送请求
     *
     * @param request
     * @param handle
     */
    public static Call get(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    /**
     * 通过构造好的Request Callback去发送请求
     *
     * @param request
     * @param handle
     * @return
     */
    public static Call post(Request request, DisposeDataHandle handle) {
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }


}
