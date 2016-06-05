package edu.scut.se.lee.util;

import android.util.Log;

/**
 * Created by jsonlee on 8/16/15.
 */
public class Data {
    public static String name = "name";
    public static double lineLength = 0;
    public static double midu = 0;
//    public static double freq = 0;
    public static JiePin[] jiePins;
    public static double ei;
    public static double f1;
    public static double f2;
    public static double f3;

    public static double getForce1() {
//        Log.i("lee.参数", midu + "," + lineLength + "," + freq);
        f1 = 0;
        for (JiePin jiePin : jiePins) {
            double force = 0.004 * midu * lineLength * lineLength * jiePin.freq * jiePin.freq / jiePin.n / jiePin.n;
            f1 += force;
        }
        f1 /= jiePins.length;
        Log.i("lee.", "平均索力f1 " + f1);
        return f1;
    }

    public static double getForce2() {
        f2 = 0;
        for (JiePin jiePin : jiePins) {
            double force = 0.004 * midu * lineLength * lineLength * jiePin.freq * jiePin.freq / jiePin.n / jiePin.n - jiePin.n * jiePin.n * ei * Math.PI * Math.PI / lineLength / lineLength * 0.001;
            f2 += force;
        }
        f2 /= jiePins.length;
        Log.i("lee.", "平均索力f2 " + f2);
        return f2;
    }

    public static double getForce3() {
        f3 = 0;
        double c = Math.sqrt(ei / midu / Math.pow(lineLength, 4));
        double kexi = Math.sqrt(f1 * 1000 / ei) * lineLength;
        for (JiePin jiePin : jiePins) {
            if (jiePin.n == 1) {
                if (kexi >= 17) {
                    f3 += 0.004 * midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (1 - 2.2 * c / jiePin.freq - 0.55 * c * c / jiePin.freq / jiePin.freq);
                } else if (kexi >= 6 && kexi <= 17) {
                    f3 += 0.004 * midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (0.865 - 11.6 * c * c / jiePin.freq / jiePin.freq);
                } else {
                    f3 += 0.004 * midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (0.828 - 10.5 * c * c / jiePin.freq / jiePin.freq);
                }
            } else if (jiePin.n == 2) {
                if (kexi >= 60) {
                    f3 += 0.001*midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (1 - 4.4 * c / jiePin.freq - 1.1 * c * c / jiePin.freq / jiePin.freq);
                } else if (kexi >= 17 && kexi <= 60) {
                    f3 += 0.001*midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (1.03 - 6.33 * c / jiePin.freq - 1.58 * c * c / jiePin.freq / jiePin.freq);
                } else {
                    f3 += 0.001*midu * jiePin.freq * jiePin.freq * lineLength * lineLength * (0.882 - 85 * c * c / jiePin.freq / jiePin.freq);
                }
            } else {
                f3 += 0.004 *midu*(jiePin.freq*jiePin.freq*lineLength*lineLength/jiePin.n/jiePin.n)*(1-2.2*c*jiePin.n/jiePin.freq);
            }
        }
        f3 /= jiePins.length;
        Log.i("lee.", "平均索力f3 " + f3);
        return f3;
    }


    //阶数和频率对
    public static class JiePin {
        int n;//阶数
        double freq;//频率

        public JiePin(int n, double freq) {
            this.n = n;
            this.freq = freq;
        }
    }

}
