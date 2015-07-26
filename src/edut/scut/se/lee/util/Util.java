package edut.scut.se.lee.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Util {
	public final static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

    public static String getPrjDir(){
        return SD_CARD_PATH+ "/"+ Cache.getInstance().load(Cache.PRJ_NAME,"acceleration");
    }

}	
