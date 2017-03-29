package ruolan.com.androidsdk.http.response;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import ruolan.com.androidsdk.ResponseEntityToModule;
import ruolan.com.androidsdk.http.exception.OkHttpException;
import ruolan.com.androidsdk.http.listener.DisposeDataHandle;
import ruolan.com.androidsdk.http.listener.DisposeDataListener;

/**
 * Created by wuyinlei on 2017/3/29.
 * <p>
 * json解析回调类
 */

public class CommonJsonCallback implements Callback {

    protected final String RESULT_CODE = "ecode";//有返回则对于http请求来说是成功的,但还是有可能是业务逻辑上的错误
    protected final int RESULT_CODE_VAULE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";
    protected final String COOKIE_STORE = "Set-Cookie"; // decide the server it


    /**
     * the java layer exception, do not same to the logic error
     */
    protected final int NETWORK_ERROR = -1; // the network relative error
    protected final int JSON_ERROR = -2; // the JSON relative error
    protected final int OTHER_ERROR = -3; // the unknow error

    /**
     * 将其它线程的数据转发到UI线程
     */
    private Handler mDeliveryHandler;
    private DisposeDataListener mListener;
    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException ioexception) {
        /**
         * 此时还在非UI线程，因此要转发
         */
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, ioexception));
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        final String result = response.body().string();
        final ArrayList<String> cookieLists = handleCookie(response.headers());

        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private ArrayList<String> handleCookie(Headers headers) {
        ArrayList<String> tempList = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.name(i).equalsIgnoreCase(COOKIE_STORE)) {
                tempList.add(headers.value(i));
            }
        }
        return tempList;
    }

    /**
     * 处理服务器返回的响应数据
     *
     * @param responseObj 返回的字符串数据
     */
    private void handleResponse(Object responseObj) {
        if (responseObj == null || responseObj.toString().trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }

        try {

            //开始尝试解析json
            JSONObject reuslt = new JSONObject(responseObj.toString());
            if (reuslt.has(RESULT_CODE)) {
                //从json对象中取出我们的响应吗  若为0  则获取正确的请求
                if (reuslt.getInt(RESULT_CODE) == RESULT_CODE_VAULE) {
                    if (mClass == null) {
                        mListener.onSuccess(responseObj);
                    } else {

                        //需要我们将json对象转化为实体对象
                        Object obj = ResponseEntityToModule.parseJsonObjectToModule(reuslt, mClass);

                        //表明了正确的转为了实体对象
                        if (obj != null) {
                            mListener.onSuccess(obj);
                        } else {
                            //返回的不是合法的json
                            mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                        }
                    }
                } else {
                    mListener.onFailure(new OkHttpException(OTHER_ERROR,reuslt.get(RESULT_CODE)));
                }
            }

        } catch (Exception e
                ) {
            e.printStackTrace();
            mListener.onFailure(new OkHttpException(OTHER_ERROR,e.getMessage()));
        }

    }
}

