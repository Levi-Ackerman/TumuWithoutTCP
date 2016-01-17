package edu.scut.se.lee.fragment;

import net.tsz.afinal.FinalActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;

public abstract class BaseFragment extends Fragment {
	protected View rootView;

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		int resId = getRootRes();
		rootView = inflater.inflate(resId, null);
		FinalActivity.initInjectedView(this, rootView);
		initData();
		return rootView;
	}

	public abstract int getRootRes();

	public abstract void initData();

	public void showMsg(final String text) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
				Log.e("Lee.",text);
			}
		});
	}
}
