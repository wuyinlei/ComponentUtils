package ruolan.com.androidsdk.http.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * Created by wuyinlei on 2017/3/29.
 *
 * @author wuyinlei
 * @function 接收请求参数, 为我们生成Request对象
 */

public class CommonRequest {

    /**
     * @param url    请求地址
     * @param params 参数
     * @return request
     */
    public static Request createPostRequest(String url, RequestParams params) {
        FormBody.Builder mFromBodyBuidld = new FormBody.Builder();
        if (params != null) {
            //将请求参数遍历添加到我们的请求构建类中
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                mFromBodyBuidld.add(entry.getKey(), entry.getValue());
            }
        }
        //通过请求构建类的build方法获取真正的请求体对象
        FormBody formBody = mFromBodyBuidld.build();

        return new Request.Builder().url(url).post(formBody).build();
    }


    /**
     * @param url    请求地址
     * @param params 传入的参数
     * @return 通过传入的参数返回一个get类型的request
     */
    public static Request createGetRequest(String url, RequestParams params) {
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        return new Request.Builder().url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .get().build();
    }
}
