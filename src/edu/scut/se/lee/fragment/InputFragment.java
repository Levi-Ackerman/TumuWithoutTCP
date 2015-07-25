package edu.scut.se.lee.fragment;

import net.tsz.afinal.annotation.view.ViewInject;
import edu.scut.se.lee.R;
import edut.scut.se.lee.util.Cache;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InputFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.et_input_linelong)
	private TextView et_linelong;
	@ViewInject(id = R.id.et_input_linename)
	private TextView et_linename;
	@ViewInject(id = R.id.btn_input_save,click="onClick")
	private Button btn_save;

	@Override
	public int getRootRes() {
		// TODO Auto-generated method stub
		return R.layout.fragment_input;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		String s;
		if (!(s = Cache.getInstance().load("linename", "null")).equals("null"))
			et_linename.setText(s);
		float f;
		if ((f = Cache.getInstance().load("linelong", -1)) != -1) {
			et_linelong.setText(f + "");
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_input_save:
			try {
				Cache.getInstance().save(
						"linelong",
						Float.parseFloat(et_linelong.getText().toString()
								.trim()));
				Cache.getInstance().save("linename",
						et_linename.getText().toString().trim());
				showMsg("保存成功");
			} catch (Exception e) {
				showMsg("数据有误");
			}
			break;
		}
	}
}
