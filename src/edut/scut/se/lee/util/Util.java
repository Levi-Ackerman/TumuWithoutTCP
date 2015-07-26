package edut.scut.se.lee.util;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.scut.se.lee.App;

public class Util {
	public final static String SD_CARD_PATH = Environment.getExternalStorageDirectory().toString();

    public static String getPrjDir(){
        return SD_CARD_PATH+ "/"+ Cache.getInstance().load(Cache.PRJ_NAME,"acceleration");
    }

    public static void showToast(String text) {
        Toast.makeText(App.getInstance(),text,Toast.LENGTH_SHORT).show();
    }

    public static void saveFileInPrjDir(String fileName,String content){
        try {
            FileWriter writer = new FileWriter(getPrjDir()+"/"+fileName);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] loadFileLinesInPrjDir(String fileName){
        try {
            FileReader reader = new FileReader(getPrjDir()+"/"+fileName);
            BufferedReader bReader = new BufferedReader(reader);
            List<String> strs = new ArrayList<String>();
            String str;
            while(null != (str = bReader.readLine()))
                strs.add(str);
            bReader.close();
            reader.close();
            return strs.toArray(new String[strs.size()]);
        } catch (Exception e) {
            return null;
        }
    }
}
