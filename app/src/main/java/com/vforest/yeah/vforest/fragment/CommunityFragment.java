package com.vforest.yeah.vforest.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.activity.CreateNewsActivity;
import com.vforest.yeah.vforest.adapter.ExhibitionAdapter;
import com.vforest.yeah.vforest.util.CommonUrl;
import com.vforest.yeah.vforest.util.Exhibition;
import com.vforest.yeah.vforest.util.HttpUtil;
import com.vforest.yeah.vforest.util.Wetland;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 11923 on 2018/3/8.
 */

public class CommunityFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Exhibition> exhibitions = new ArrayList<>();
    private ListView exhibitonLv;
    private ImageButton publish;
    private ExhibitionAdapter exhibitionAdapter;
    ImageButton create;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_community, container, false);
        exhibitonLv = findViewById(R.id.exhibitionListView);
        exhibitonLv.setDividerHeight(2);
        initExhibitions();
        exhibitionAdapter = new ExhibitionAdapter(getContext(),R.layout.exhibit_item, exhibitions);
        exhibitonLv.setAdapter(exhibitionAdapter);
        create = findViewById(R.id.btn_publish);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateNewsActivity.class);
                startActivity(intent);

            }
        });
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        return mRootView;
    }

    private void initExhibitions(){
        //Exhibition e1 = new Exhibition("小喵喵","植物长得还可以",R.drawable.temp1,R.drawable.temp2, R.drawable.temp3);
        new Thread(){
            @Override
            public void run() {
                HttpUtil.sendOkHttpRequest(CommonUrl.getNewsList, new HashMap<String, String>(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("!!!", "onFailure: 失败啦！！！！！");
                        indicate("获取动态列表失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d("!!!!!!!!!", "onResponse: 成功啦！！！！！");
                        final String responseText = response.body().string();
                        Log.d("!!!!!!!!!!!", "initExhibition: " + responseText);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject res = new JSONObject(responseText);
                                    JSONArray list = res.getJSONArray("contents");
                                    Log.d("!!!!!!!", "run: "+list.length());
                                    for (int i = 0; i < list.length(); i++) {
                                        JSONObject exhibition = list.getJSONObject(i);
                                        String des = exhibition.getString("description");
                                        String usr = exhibition.getString("user");
                                        String time = exhibition.getString("time");
                                        JSONArray uris = exhibition.getJSONArray("uri");
                                        ArrayList<String> tokens = new ArrayList<String>();
                                        for(int j = 0;j < uris.length();j++){
                                            tokens.add(uris.getString(j));
                                        }
                                        Exhibition ex = new Exhibition(usr, des, time, tokens);
                                        exhibitions.add(ex);
                                    }
                                    exhibitionAdapter.notifyDataSetChanged();

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
        exhibitions.clear();
        initExhibitions();
    }

    private void indicate(final String hint){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(mActivity, hint,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

