package com.vforest.yeah.vforest.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.vforest.yeah.vforest.R;

import com.vforest.yeah.vforest.fragment.BaseFragment;
import com.vforest.yeah.vforest.fragment.CommunityFragment;
import com.vforest.yeah.vforest.fragment.MyselfFragment;
import com.vforest.yeah.vforest.fragment.WetlandFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fragmentManager=this.getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;
    private ImageButton[] BottomButton;
    private BaseFragment[] fragmentGroup=new BaseFragment[3];
    private static final String TAG = "MainActivity";
    private static int select=0;//为了进入app后直接进入湿地管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if(0==select){
            select=1;
            BottomButton[2].callOnClick();}
        int cuttent=0;
        for(BaseFragment baseFragment:fragmentGroup){
            if(baseFragment.isHidden())
                cuttent++;
        }
        Log.d(TAG, "onResume: "+cuttent);
        if(cuttent<=3){
            hidefragment();
            BottomButton[2].callOnClick();
        }*/
        BottomButton[0].callOnClick();
    }

    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragmentGroup[0]==null&&fragment instanceof WetlandFragment)
            fragmentGroup[0]=(BaseFragment) fragment;
        if(fragmentGroup[1]==null&&fragment instanceof CommunityFragment)
            fragmentGroup[1]=(BaseFragment) fragment;
        if(fragmentGroup[2]==null&&fragment instanceof MyselfFragment)
            fragmentGroup[2]=(BaseFragment) fragment;
    }

    private void initView(){
        BottomButton=new ImageButton[3];
        BottomButton[0]= (ImageButton) findViewById(R.id.button_wetland);
        BottomButton[1]= (ImageButton) findViewById(R.id.button_explore);
        BottomButton[2]= (ImageButton) findViewById(R.id.button_myself);
        for(ImageButton i:BottomButton)
            i.setOnClickListener(this);
    }

    private void initData(){
        hidefragment();
    }


    @Override
    public void onClick(View v) {
        hidefragment();
        fragmentTransaction=fragmentManager.beginTransaction();
        for(ImageButton i:BottomButton)
            i.setSelected(false);
        switch (v.getId()){
            case R.id.button_wetland:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("fg_wetland"));
                fragmentTransaction.commit();
                BottomButton[0].setSelected(true);
                break;
            case R.id.button_explore:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("fg_explore"));
                fragmentTransaction.commit();
                BottomButton[1].setSelected(true);
                break;
            case R.id.button_myself:
                fragmentTransaction.show(fragmentManager.findFragmentByTag("fg_myself"));
                fragmentTransaction.commit();
                BottomButton[2].setSelected(true);
                break;
            default:
                Log.d(TAG, "onClick: other click");
                break;
        }
    }
    /*
    * 初始化创建所有的fragment，隐藏fragment
    * */
    private void hidefragment(){
        fragmentTransaction=fragmentManager.beginTransaction();
        String a[]={"fg_wetland","fg_explore","fg_myself"};
        for(int i=0;i<3;++i){
            if(fragmentGroup[i]==null){
                if(0==i){
                    fragmentGroup[i]= new WetlandFragment();
                }
                else if(1==i)
                    fragmentGroup[i]=new CommunityFragment();
                else
                    fragmentGroup[i]=new MyselfFragment();

                fragmentTransaction.add(R.id.fragmentlayout_content,fragmentGroup[i],a[i]);
            }
            else
                fragmentTransaction.hide(fragmentManager.findFragmentByTag(a[i]));
        }
        fragmentTransaction.commit();
    }

}
