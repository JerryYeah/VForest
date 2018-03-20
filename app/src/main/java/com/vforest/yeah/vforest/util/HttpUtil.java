package com.vforest.yeah.vforest.util;


import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * Created by 11923 on 2018/3/3.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address, final Map<String, String> params, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder builder = new FormBody.Builder();
        if(params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(address).post(builder.build()).build();
        client.newCall(request).enqueue(callback);
    }
}
