package com.vforest.yeah.vforest.util;

/**
 * Created by 11923 on 2018/3/3.
 */

import com.google.gson.Gson;

public class JsonResponse {

    public String code;
    public String contents;

    public static String getRspCode(String rsp){
        Gson gson = new Gson();
        JsonResponse response = gson.fromJson(rsp, JsonResponse.class);
        return response.code;
    }

}
