package edut.scut.se.lee.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Cache {
	public static Cache instance;
	public SharedPreferences sp;
    public static final String PRJ_NAME = "prj_name";

	/**
	 * 在App初始化时调用，其他地方请勿调用
	 * 
	 * @param context
	 * @return 完成初始化动作返回true，若已经被初始化过则返回false
	 */
	public static boolean init(Context context) {
		if (instance == null) {
			instance = new Cache();
			instance.sp = context.getSharedPreferences("cache",
					Context.MODE_PRIVATE);
			return true;
		} else {
			return false;
		}
	}

	public void save(String key, String value) {
		sp.edit().putString(key, value).commit();
	}

	public void save(String key, float value) {
		sp.edit().putFloat(key, value).commit();
	}

	public String load(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public float load(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}

	public static Cache getInstance() {
		if (instance == null) {
			try {
				throw new Exception("Inited Cache Expected!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}

}
