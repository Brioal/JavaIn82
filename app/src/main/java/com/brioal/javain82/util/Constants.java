package com.brioal.javain82.util;

import android.content.Context;

/**
 * Created by Brioal on 2016/5/6.
 */
public class Constants {
    public static final String APPID = "11e38ebdbb1916df56f752a94be45e67";
    private static DataLoader mDataLoader ;

    public static DataLoader getDataLoader(Context context) {
        if (mDataLoader == null) {
            mDataLoader = new DataLoader(context);
        }
        return mDataLoader;
    }
}
