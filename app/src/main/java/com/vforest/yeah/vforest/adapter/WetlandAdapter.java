package com.vforest.yeah.vforest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.util.Wetland;

import java.util.List;

/**
 * Created by 11923 on 2018/3/8.
 */

public class WetlandAdapter extends ArrayAdapter<Wetland> {

    private int resourceId;

    public WetlandAdapter(Context context, int resource, List<Wetland> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Wetland wetland = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView name, temp, humi, illum;
        name = (TextView) view.findViewById(R.id.wetland_name);
        temp = (TextView) view.findViewById(R.id.temperature_data);
        humi = (TextView) view.findViewById(R.id.humidity_data);
        illum = (TextView) view.findViewById(R.id.illumination_data);
        // set values
        name.setText(wetland.getName());
        temp.setText(String.valueOf(wetland.getTemp())+"℃");
        humi.setText(String.valueOf(wetland.getWet())+"%");
        illum.setText(String.valueOf(wetland.getIllum())+"Lx");
        if(humi.getText().toString().compareTo("20.0")<0) {
            view.setBackgroundColor(Color.rgb(255,99,71));
        }
        return view;
    }
}
