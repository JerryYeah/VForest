package com.vforest.yeah.vforest.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.activity.LoginActivity;
import com.vforest.yeah.vforest.activity.MainActivity;
import com.vforest.yeah.vforest.activity.WetlandDetailActivity;
import com.vforest.yeah.vforest.adapter.WetlandAdapter;
import com.vforest.yeah.vforest.util.CommonUrl;
import com.vforest.yeah.vforest.util.HttpUtil;
import com.vforest.yeah.vforest.util.JsonResponse;
import com.vforest.yeah.vforest.util.Wetland;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
/**
 * Created by 11923 on 2018/3/7.
 */

public class WetlandFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Wetland> wetlands = new ArrayList<>();
    ImageButton add;
    private ListView wetlandLv;
    private WetlandAdapter wlAdapter;
    //private EditText numberEt, nameEt;
    private boolean flag =false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_wetland, container, false);
        wetlandLv = findViewById(R.id.wetlandListView);
        wlAdapter = new WetlandAdapter(getContext(),R.layout.wetland_item, wetlands);
        wetlandLv.setAdapter(wlAdapter);
        add = findViewById(R.id.btn_add_wetland);
//        LayoutInflater factory = LayoutInflater.from(mActivity);
//        View layout = factory.inflate(R.layout.add_wetland_dialog, null);
//        numberEt = (EditText) layout.findViewById(R.id.device_num);
//        nameEt = (EditText) layout.findViewById(R.id.wetland_name);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_wt);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setRefreshing(true);
        LayoutInflater factory = LayoutInflater.from(mActivity);
        final View layout = factory.inflate(R.layout.add_wetland_dialog, null);
        final EditText numberEt = (EditText) layout.findViewById(R.id.device_num);
        final EditText nameEt = (EditText) layout.findViewById(R.id.wetland_name);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ViewParent vp = layout.getParent();
                if(vp instanceof ViewGroup){
                    ((ViewGroup)vp).removeAllViews();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("添加湿地");
                builder.setView(layout);
                builder.setCancelable(true);
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String num = numberEt.getText().toString().trim();
                        String wetland_name = nameEt.getText().toString().trim();
                        String user_name = LoginActivity.getName();

                        if(!num.equals("") && !wetland_name.equals("")){
                            final Map<String,String> map = new HashMap();
                            map.put("username", user_name);
                            map.put("sequence", num);
                            map.put("name", wetland_name);
                            Log.d("begin to send request", "before sending: ");
                            HttpUtil.sendOkHttpRequest(CommonUrl.addWetland, map, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("!!!", "onFailure: 失败啦！！！！！");
                                    indicate("添加湿地失败");
                                }
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("!!!!!!!!!", "onResponse: 成功啦！！！！！");
                                    String responseText = response.body().string();
                                    String code = JsonResponse.getRspCode(responseText);
                                    Log.d("!!!!!!!!!", "回复:" + code);
                                    if (code.equals("11001")) {     //成功添加
                                        indicate("添加成功");
                                    }
                                    else if(code.equals("11000")){
                                        indicate("该设备已被注册");
                                    }else
                                        indicate("添加湿地失败");
                                }
                            });

                        } else
                            Toast.makeText(mActivity, "请输入必要信息", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            }
        });
        initWetlands();
        wetlandLv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wetland wetland = wetlands.get(position);
                String wetland_name = wetland.getName();
                Float temp = wetland.getTemp();
                Float hummi = wetland.getWet();
                Float illum = wetland.getIllum();
                Log.d("!!!!!!!!", "onItemClick: "+wetland_name);
                Log.d("!!!!!!!!", "onItemClick: "+temp);
                Intent i = new Intent(mActivity, WetlandDetailActivity.class);
                i.putExtra("wetland_name",wetland_name);
                i.putExtra("temperature",temp);
                i.putExtra("humidity",hummi);
                i.putExtra("illumination",illum);
                startActivity(i);
            }
        });
    }

    private void  initWetlands(){
        //Wetland wt1 = new Wetland("青青草地", 30, 65, 450);
        final Map<String, String> map = new HashMap();
        String user_name = LoginActivity.getName();
        map.put("username", user_name);
        new Thread(){
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest(CommonUrl.getWetlandList, map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("!!!", "onFailure: 失败啦！！！！！");
                        indicate("获取我的湿地失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("!!!!!!!!!", "onResponse: 成功啦！！！！！");
                        final String responseText = response.body().string();
                        Log.d("!!!!!!!!!!!", "initWetlands: "+responseText);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject res = new JSONObject(responseText);
                                    JSONArray list = res.getJSONArray("contents");
                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject wetland = list.getJSONObject(i);
                                        String name = wetland.getString("name");
                                        String temp = wetland.getString("temp");
                                        String humi = wetland.getString("humi");
                                        String illum = wetland.getString("illum");
                                        Wetland wt = new Wetland(name, new Float(temp), new Float(humi),
                                                new Float(illum));
                                        wetlands.add(wt);
                                    }
                                    wlAdapter.notifyDataSetChanged();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    }
                });
            }
        }.start();
        //wetlands.add(wt1);
    }

    @Override
    public void onRefresh() {
        wetlands.clear();
        initWetlands();
    }

    private void indicate(final String hint){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, hint,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}

