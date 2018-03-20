package com.vforest.yeah.vforest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vforest.yeah.vforest.R;
import com.vforest.yeah.vforest.activity.CreateNewsActivity;
import com.vforest.yeah.vforest.adapter.ExhibitionAdapter;
import com.vforest.yeah.vforest.util.Exhibition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11923 on 2018/3/8.
 */

public class CommunityFragment extends BaseFragment {

    private List<Exhibition> exhibitions = new ArrayList<>();
    private ListView exhibitonLv;
    private ImageButton publish;
    private ExhibitionAdapter exhibitionAdapter;
    ImageButton create;

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
        return mRootView;
    }

    private void initExhibitions(){
        Exhibition e1 = new Exhibition("植物长得还可以",R.drawable.temp1,R.drawable.temp2, R.drawable.temp3);
        exhibitions.add(e1);

    }
}
