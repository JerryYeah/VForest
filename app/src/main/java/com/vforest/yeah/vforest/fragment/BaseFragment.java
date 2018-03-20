package com.vforest.yeah.vforest.fragment;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by 11923 on 2018/3/7.
 */

public  class BaseFragment extends android.support.v4.app.Fragment {
    /**
     * 贴附的activity
     */
    protected FragmentActivity mActivity;
    /**
     * 根view
     */
    protected View mRootView;

    /**
     * 获取依赖的Activity，防止返回请求到的是空的null
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        mActivity = getActivity();
    }


    @SuppressWarnings("unchecked")
    protected <T extends View> T findViewById(int id)
    {
        if (mRootView == null)
        {
            return null;
        }

        return (T) mRootView.findViewById(id);
    }

}
