package com.example.photoupload;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sharedpref on 2/12/19.
 */
public class Sharedpref {

    Context context;
    public static final String imagename= "imagename";
    public static final String mypref ="mypref";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public Sharedpref(Context context)
    {
        this.context = context;
        sharedPreferences= context.getSharedPreferences(mypref,Context.MODE_PRIVATE);
    }

    public  String getImagename() {
        String value = sharedPreferences.getString(imagename,null);
        return value;
    }
    public  void setImagename(String value){
        editor=sharedPreferences.edit();
        editor.putString(imagename,value);
        editor.apply();

    }
}
