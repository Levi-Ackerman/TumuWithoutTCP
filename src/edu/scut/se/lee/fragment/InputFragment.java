package edu.scut.se.lee.fragment;

import net.tsz.afinal.annotation.view.ViewInject;
import edu.scut.se.lee.R;
import edu.scut.se.lee.util.Cache;
import edu.scut.se.lee.util.Util;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InputFragment extends BaseFragment implements OnClickListener {

	@ViewInject(id = R.id.et_input_linelong)
	private TextView mEditLineLong;
    private final String lineLong = "索长=";
	@ViewInject(id = R.id.et_input_linename)
	private TextView mEditLineName;
    private final String lineName = "索名=";
    @ViewInject(id=R.id.et_input_density)
    private TextView mEditDensity;
    private final String density = "密度=";
    @ViewInject(id=R.id.et_input_strength)
    private TextView mEditStrength;
    private final String strength = "强度=";
	@ViewInject(id = R.id.btn_input_save,click="onClick")
	private Button btn_save;
    @ViewInject(id=R.id.btn_input_load,click = "onClick")
    private Button btn_load;

	@Override
	public int getRootRes() {
		// TODO Auto-generated method stub
		return R.layout.fragment_input;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

    }

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
            case R.id.btn_input_save:
                try {
                    String content = lineName + mEditLineName.getText().toString().trim() + "\n";
                    content += lineLong + mEditLineLong.getText().toString().trim() + "\n";
                    content += density + mEditDensity.getText().toString().trim() + "\n";
                    content += strength + mEditStrength.getText().toString().trim() + "\n";
                    Util.saveFileInPrjDir("BaseInfo.txt", content);
                    Util.showToast("保存基本信息为BaseInfo.txt成功");
                } catch (Exception e) {
                    Util.showToast("保存基本信息失败");
                }
                break;

            case R.id.btn_input_load:
                String[] strs = Util.loadFileLinesInPrjDir("BaseInfo.txt");
                if (strs == null || strs.length != 4) {
                    Util.showToast("没有已保存的数据");
                    return;
                }
                String[] names = strs[0].split("=");
                mEditLineName.setText(names.length == 2 ? names[1] : "0");
                String[] longs = strs[1].split("=");
                mEditLineLong.setText(longs.length == 2 ? longs[1] : "0");
                String[] densities = strs[2].split("=");
                mEditDensity.setText(densities.length == 2 ? densities[1] : "0");
                String[] strengths = strs[3].split("=");
                mEditStrength.setText(strengths.length == 2 ? strengths[1] : "0");
                Util.showToast("数据加载完成");
                break;
        }
	}
}
