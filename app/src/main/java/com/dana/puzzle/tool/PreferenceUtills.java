package com.dana.puzzle.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.dana.puzzle.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PreferenceUtills {
    private static final String TAG = "pref_utls";

    private static final String NAME = "pref";
    private final SharedPreferences mSharedPref;
    private static PreferenceUtills mPrefUtils;





    private PreferenceUtills(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveSharedPreferencesFrom(context, NAME)) {
                Log.w("TAG", "Failed to migrate shared preferences.");
            }
            context = deviceContext;
        }

        mSharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceUtills getInstance(Context context) {
        if (mPrefUtils == null)
            mPrefUtils = new PreferenceUtills(context);
        return mPrefUtils;
    }





    public void setboolean(String key,boolean value)
    {
        mSharedPref.edit().putBoolean(key,value ).apply();
    }

    public boolean getBoolean(String key)
    {
        return mSharedPref.getBoolean(key,true);

    }


    // Date validate
    public  void setValidDateInPreference(String key, String date)
    {
        mSharedPref.edit().putString(key, date).apply();
    }

    public boolean IsValidDateByKey(String key)
    {
        if (mSharedPref.getString(key,null)==null)
        {
            return false;
        }

        else
        {
            Date todayDate;

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT_PREFERENCE,Locale.ENGLISH);

            try {
                todayDate = dateFormatter.parse( dateFormatter.format(new Date()));
                Date pramotionDate = dateFormatter.parse(mSharedPref.getString(key,new Date().toString()));

                if(todayDate != null)
                    return todayDate.equals(pramotionDate);

            } catch (ParseException e) {

                return  false;
            }

        }

        return false;
    }


}
