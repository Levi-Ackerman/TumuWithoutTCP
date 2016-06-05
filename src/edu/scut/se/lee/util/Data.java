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
    public static double freq = 0;
    public static JiePin[] jiePins;

    public static double getForce(){
        Log.i("lee.参数",midu+","+lineLength+","+ freq);
        int avgForce = 0;
        for (JiePin jiePin : jiePins) {
            double force = 0.004*midu*lineLength*lineLength* jiePin.freq * jiePin.freq /jiePin.n/jiePin.n;
            avgForce += force;
        }
        avgForce /= jiePins.length;
        Log.i("lee.","平均索力"+avgForce);
        return avgForce;
    }

    //阶数和频率对
    public static class JiePin{
        int n;//阶数
        double freq;//频率
        public JiePin(int n,double freq){
            this.n = n ;
            this.freq = freq;
        }
    }

}
