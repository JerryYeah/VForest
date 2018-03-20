package com.vforest.yeah.vforest.util;

import android.widget.EditText;

/**
 * Created by 11923 on 2018/3/9.
 */

public class Exhibition {
    private String description;
    private int pic1, pic2, pic3;  //最多存三张照片

    public Exhibition(String s, int image1, int image2, int image3){
        description =s;
        pic1 = image1;
        pic2 = image2;
        pic3 = image3;
    }

    public String getDescription() {
        return description;
    }

    public int getPic1() {
        return pic1;
    }

    public int getPic2() {
        return pic2;
    }

    public int getPic3() {
        return pic3;
    }
}
