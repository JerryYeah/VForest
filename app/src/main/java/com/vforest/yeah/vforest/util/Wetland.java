package com.vforest.yeah.vforest.util;

/**
 * Created by 11923 on 2018/3/6.
 */

public class Wetland {
    private String name;
    private float temp;
    private float wet;
    private float illum;

    public Wetland(String s, float a, float b, float c){
        name=s;
        temp=a;
        wet=b;
        illum=c;
    }

    public String getName() {
        return name;
    }

    public float getTemp() {
        return temp;
    }

    public float getWet() {
        return wet;
    }

    public float getIllum() {
        return illum;
    }
}
