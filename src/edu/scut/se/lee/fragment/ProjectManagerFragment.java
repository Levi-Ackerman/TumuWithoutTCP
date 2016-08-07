package edu.scut.se.lee.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.tsz.afinal.annotation.view.ViewInject;

import java.io.File;

import edu.scut.se.lee.R;
import edu.scut.se.lee.util.Cache;
import edu.scut.se.lee.util.Util;

/**
 * Created by lizhengxian on 16/3/26.
 */
public class ProjectManagerFragment extends BaseFragment implements View.OnClickListener {
    @ViewInject(id = R.id.btnOk, click = "onClick")
    private Button btnOk;
    @ViewInject(id = R.id.et_project_name)
    private EditText etPrjName;

    @Override
    public int getRootRes() {
        return R.layout.fragment_project_manager;
    }

    @Override
    public void initData() {
        etPrjName.setText(Cache.getInstance().load(Cache.PRJ_NAME, ""));
    }

    @Override
    public void onClick(View view) {

        String name = etPrjName.getText().toString().trim();
        if (name == null || name.equals("")) {
            Util.showToast("项目名字不能为空");
            return;
        }
        Cache.getInstance().save(Cache.PRJ_NAME, name);
        File file = new File(Util.getPrjDir());
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }
}
