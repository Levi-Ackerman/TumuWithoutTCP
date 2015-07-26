package edut.scut.se.lee.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

import edu.scut.se.lee.App;

public class Util {
	public final static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

    public static String getPrjDir(){
        return SD_CARD_PATH+ "/"+ Cache.getInstance().load(Cache.PRJ_NAME,"acceleration");
    }

    public static void showToast(String text) {
        Toast.makeText(App.getInstance(),text,Toast.LENGTH_SHORT).show();
    }
}
