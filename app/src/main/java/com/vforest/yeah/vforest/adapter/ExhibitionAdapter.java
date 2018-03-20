package com.vforest.yeah.vforest.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.util.Exhibition;
import com.vforest.yeah.vforest.util.Wetland;
import com.vforest.yeah.vforest.view.SquareImageView;

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
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView description, publish_time;
        ImageView []pic = new ImageView[3];
        description = (TextView) view.findViewById(R.id.description);
        pic[0] = (ImageView) view.findViewById(R.id.photoset_img_1);
        pic[1] = (ImageView) view.findViewById(R.id.photoset_img_2);
        pic[2] = (ImageView) view.findViewById(R.id.photoset_img_3);
        publish_time = (TextView) view.findViewById(R.id.publish_time);
        // set values
        description.setText(exhibition.getDescription());
        pic[0].setImageResource(exhibition.getPic1());
        pic[1].setImageResource(exhibition.getPic2());
        pic[2].setImageResource(exhibition.getPic3());
        //setPublishTime();
        return view;
    }

}
