package edu.scut.se.lee.util;

import android.util.Log;

/**
 * Created by jsonlee on 8/16/15.
 */
public class Data {
    public static String name = "name";
    public static double lineLength = 0;
    public static double midu = 0;
//    public static double kangwanqiangdu = 0;
    public static double avgFreq = 0;
    public static double getForce(){
        Log.i("lee.参数",midu+","+lineLength+","+avgFreq);
        return 0.004*midu*lineLength*lineLength*avgFreq*avgFreq;
    }
}
