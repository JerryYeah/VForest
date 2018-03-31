package com.vforest.yeah.vforest.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.util.Exhibition;
import com.vforest.yeah.vforest.util.Wetland;
import com.vforest.yeah.vforest.view.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11923 on 2018/3/9.
 */

public class ExhibitionAdapter extends ArrayAdapter<Exhibition> {
    private int resourceId;

    public ExhibitionAdapter(Context context, int resource, List<Exhibition> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exhibition exhibition = getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.setValues(exhibition);
        return view;
    }

    class ViewHolder{

        TextView uname, description, publish_time;
        ImageView []pic = new ImageView[3];

        public ViewHolder(View view){
            uname = (TextView) view.findViewById(R.id.news_uname);
            description = (TextView) view.findViewById(R.id.description);
            publish_time = (TextView) view.findViewById(R.id.publish_time);
            pic[0] = (ImageView) view.findViewById(R.id.photoset_img_1);
            pic[1] = (ImageView) view.findViewById(R.id.photoset_img_2);
            pic[2] = (ImageView) view.findViewById(R.id.photoset_img_3);
        }

        void setValues(Exhibition e){
            uname.setText(e.getUsr());
            description.setText(e.getDescription());
            //setPublishTime();
            setTime(e.getCrateTime(), publish_time);
            //显示图片
            for(int m=0;m<3;m++)
                pic[m].setVisibility(View.INVISIBLE);
            final ArrayList<String> tokens = e.getTokenList();
            for(int i=0;i<tokens.size();i++){
                Glide.with(getContext()).load(tokens.get(i)).into(pic[i]);
                pic[i].setVisibility(View.VISIBLE);
                final int j =i;
                pic[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog=new Dialog(getContext(),android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        ImageView iv =new ImageView(getContext());
                        Glide.with(getContext()).load(tokens.get(j)).into(iv);
                        dialog.setContentView(iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
            }
        }

        void setTime(String time, TextView tv){
            Time t = new Time();
            t.setToNow();
            int year = t.year;
            int month = t.month;
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;

            int ryear = Integer.parseInt(time.substring(0,4));
            int rmonth = Integer.parseInt(time.substring(5,7));
            int rdate = Integer.parseInt(time.substring(8,10));
            int rhour = Integer.parseInt(time.substring(11,13));
            int rminute = Integer.parseInt(time.substring(14,16));

            if(year>ryear) {
                tv.setText(ryear + "年");
            }
            else if(month>rmonth){
                tv.setText(ryear+"年"+rmonth+"月");
            }else if(date>rdate){
                if(date-1==rdate)
                    tv.setText("昨天");
                else
                    tv.setText(rmonth+"月"+rdate+"日");
            }else if(hour>rhour){
                tv.setText(rhour+":"+rminute);
            }else if(minute>rminute){
                tv.setText((minute-rminute)+"分钟前");
            }else{
                tv.setText("刚刚");
            }

        }
    }

}
