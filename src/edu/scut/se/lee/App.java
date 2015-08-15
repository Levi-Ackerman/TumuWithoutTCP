package edu.scut.se.lee;

import edu.scut.se.lee.util.Cache;
import android.app.Application;

public class App extends Application {
	private static App instance = null;
	public static App getInstance(){
		return instance;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		Cache.init(getApplicationContext());
	}
}
