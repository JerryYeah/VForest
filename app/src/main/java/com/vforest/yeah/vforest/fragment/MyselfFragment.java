package com.vforest.yeah.vforest.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.activity.LoginActivity;


public class MyselfFragment extends BaseFragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_myself, container, false);
        TextView confirm, logoff;
        confirm = (TextView) findViewById(R.id.confirm);
        logoff = (TextView) findViewById(R.id.logoff);
        confirm.setOnClickListener(this);
        logoff.setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm:
                AlertDialog.Builder builder0 = new AlertDialog.Builder(mActivity).setTitle("确定已经种植？？");

                builder0.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
                        t.setToNow(); // 取得系统时间。
                        //int month = t.month;
                        int date = t.monthDay;
                        SharedPreferences pre = mActivity.getSharedPreferences("confirm", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pre.edit();
                        editor.putString("isConfirmed", "yes");
                        editor.putInt("day",date);
                        editor.commit();
                    }
                });
                builder0.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog0 = builder0.create();
                dialog0.setCancelable(false);
                dialog0.show();
                dialog0.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog0.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                break;

            case R.id.logoff:
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle("确定退出登录？");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCancelable(true);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                break;
            default:
                break;

        }
    }
}
