package edu.scut.se.lee.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.tsz.afinal.annotation.view.ViewInject;

import edu.scut.se.lee.R;
import edu.scut.se.lee.util.DB;
import edu.scut.se.lee.util.Data;
import edu.scut.se.lee.util.FFT;
import edu.scut.se.lee.util.Util;

/**
 * Created by jsonlee on 7/26/15.
 */
public class ForceComputeFragment extends BaseFragment implements View.OnClickListener {
    @ViewInject(id = R.id.btn_force_compute, click = "onClick")
    private Button btnComputeResult;
    @ViewInject(id = R.id.tvName)
    private TextView tvName;
    @ViewInject(id=R.id.tvFrequence)
    private TextView tvFrequence;
    @ViewInject(id = R.id.tvResult1)
    private TextView tvResult1;
    @ViewInject(id = R.id.tvResult2)
    private TextView tvResult2;

    @Override
    public int getRootRes() {
        return R.layout.fragment_force_compute;
    }

    @Override
    public void initData() {
        tvName.setText(Data.name);
        tvFrequence.setText(Data.frequence+"");
    }

    private double calculate1() {
        double t = 4 * Data.midu * Data.lineLength *Data.lineLength;
        return t;
    }

    private double calculate2() {
        double t = calculate1()+0;
        return t;
    }

    @Override
    public void onClick(View v) {
        Util.showToast("计算中，请稍候");
        double res1 = calculate1();
        double res2 = calculate2();
        tvResult1.setText(res1+"");
        tvResult2.setText(res2+"");
        DB.putResult(new DB.Result(Data.name, Data.frequence, res1, res2));
        int[] a = {1,1,1,1,1,1};
        try {
            new FFT(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
