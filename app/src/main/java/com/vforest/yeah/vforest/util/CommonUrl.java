package com.vforest.yeah.vforest.util;

/**
 * Created by 11923 on 2018/3/3.
 */

public class CommonUrl {
    private static final String netUrl="http://223.3.124.220:8080/";   // 193.112.7.131
    public static final String url = netUrl;
    public static final String loginAccount = url + "login";//登录接口
    public static final String registerAccount = url + "register";//注册接口
    public static final String getWetlandList = url + "getWetlandList";//获取我的湿地接口
    public static final String addWetland = url + "addWetland";//添加湿地接口
    public static final String uploadNews = url + "uploadNews";//上传动态接口
    public static final String getNewsList = url + "getNewsList";//获取动态接口
}
