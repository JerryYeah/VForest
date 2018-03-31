package com.vforest.yeah.vforest.util;

import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by 11923 on 2018/3/9.
 */

public class Exhibition {
    private String description, usr, crateTime;
    private ArrayList<String> tokenList;  //最多存三张照片

    public Exhibition(String u, String d, String t,ArrayList<String> tokens ) {
        usr = u;
        description = d;
        crateTime =t;
        tokenList = tokens;
    }

    public String getUsr() {
        return usr;
    }

    public String getDescription() {
        return description;
    }

    public String getCrateTime() {
        return crateTime;
    }

    public ArrayList<String> getTokenList() {
        return tokenList;
    }
}