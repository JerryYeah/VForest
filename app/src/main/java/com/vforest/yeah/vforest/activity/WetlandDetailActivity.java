package com.vforest.yeah.vforest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vforest.yeah.vforest.R;

public class WetlandDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView wtname, temp, humi, illum, temp_advice, humi_advice, illum_advice,comment;
    boolean notGood = false;
    private ImageButton back, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wetland_detail);
        back = (ImageButton) findViewById(R.id.btn_back_wt);
        delete = (ImageButton) findViewById(R.id.btn_delete_wt);
        wtname = (TextView) findViewById(R.id.wt_name);
        temp = (TextView) findViewById(R.id.temp_data);
        humi = (TextView) findViewById(R.id.humi_data);
        illum = (TextView) findViewById(R.id.illum_data);
        temp_advice = (TextView) findViewById(R.id.temperature_extra);
        humi_advice = (TextView) findViewById(R.id.humidity_extra);
        illum_advice = (TextView) findViewById(R.id.illumination_extra);
        comment = (TextView) findViewById(R.id.comments);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        Intent intent = getIntent();
        wtname.setText(intent.getStringExtra("wetland_name"));
        float t,h,i;
        t = intent.getFloatExtra("temperature",0);
        h = intent.getFloatExtra("humidity",0);
        i =  intent.getFloatExtra("illumination",0);
        temp.setText(String.valueOf(t)+"℃");
        humi.setText(String.valueOf(h)+"%");
        illum.setText(String.valueOf(i)+"Lx");
        StringBuffer buf=new StringBuffer();
        if(t<10) {
            notGood =true;
            temp_advice.setText("环境温度低，注意植物防寒");
            buf.append("周围环境温度低，为了植物的健康生长，请做好保暖措施  " );
        }
        else if(t>35) {
            notGood =true;
            temp_advice.setText("环境温度高，请注意给植物浇水哦");
            buf.append("环境温度高，记得勤给植物浇水哦  ");
        }
        if(h<20) {
            notGood =true;
            humi_advice.setText("环境湿度低，注意给植物浇水哦");
            buf.append(" 环境湿度低，防止植物干涸，请提高环境湿度");
        }
        if(notGood)
            comment.setText(buf.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back_wt:
                this.finish();
                break;
            //case R.id.btn_delete_wt: 暂不实现
            default:
                break;
        }

    }
}
