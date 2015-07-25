package edu.scut.se.lee.fragment;

import java.io.FileReader;
import java.io.FileWriter;

import android.os.Environment;
import android.widget.TextView;
import net.tsz.afinal.annotation.view.ViewInject;
import edu.scut.se.lee.R;

public class ArrayDisplayFragment extends BaseFragment {

	@ViewInject(id = R.id.tv_acc)
	private TextView tv_acc;

	@Override
	public int getRootRes() {
		// TODO Auto-generated method stub
		return R.layout.fragment_array_display;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		try {
			FileReader fileReader = new FileReader(Environment
					.getExternalStorageDirectory().toString()
					+ "/AccelerateData.txt");
			char[] buf = new char[1024];
			int length;
			StringBuilder sb = new StringBuilder();
			while ((length = fileReader.read(buf)) > -1) {
				sb.append(new String(buf, 0, length));
			}
			tv_acc.setText(sb.toString());
			fileReader.close();
		} catch (Exception e) {

		}
	}

}
