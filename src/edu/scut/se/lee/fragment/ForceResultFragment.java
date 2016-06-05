package edu.scut.se.lee.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.scut.se.lee.App;
import edu.scut.se.lee.R;
import edu.scut.se.lee.util.Cache;
import edu.scut.se.lee.util.DB;
import edu.scut.se.lee.util.Data;
import edu.scut.se.lee.util.Util;

/**
 * Created by jsonlee on 7/26/15.
 */
public class ForceResultFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private SimpleAdapter adapter;
    @ViewInject(id = R.id.lvResult, itemClick = "onItemClick")
    private ListView mLVResult;
    @ViewInject(id = R.id.btn_export_result, click = "onClick")
    private Button btnLoadResult;
    private List<Map<String, Object>> items;

    @Override
    public int getRootRes() {
        return R.layout.fragment_force_result;
    }

    @Override
    public void initData() {
        items = new ArrayList<Map<String, Object>>();
        initItems();
        adapter = new SimpleAdapter(App.getInstance(), items, R.layout.item_result, new String[]{"name", "force1", "force2", "force3"}, new int[]{R.id.tv0,  R.id.tv2, R.id.tv3, R.id.tv4});
        mLVResult.setAdapter(adapter);
        load();
    }

    private void initItems() {
        items.clear();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "索编号");
        item.put("force1", "公式1");
        item.put("force2", "公式2");
        item.put("force3", "公式3");
        items.add(item);
    }

    @Override
    public void onClick(View v) {
//        load();
        StringBuilder builder = new StringBuilder("索编号\t公式1\t公式2\t公式3\n");
        List<DB.Result> ltRes = DB.getResults();
        for (DB.Result ltRe : ltRes) {
            //第一行是表头
            builder.append(ltRe.getName()).append("\t")
                    .append(ltRe.getForce1()).append("\t")
                    .append(ltRe.getForce2()).append("\t")
                    .append(ltRe.getForce3()).append("\t\n");
        }
        Util.saveFileInPrjDir(Data.name+"索力结果"+".txt",builder.toString());
        showMsg("保存成功");
    }

    public void load() {
        initItems();
        List<DB.Result> ltRes = DB.getResults();
        for (DB.Result res : ltRes) {
            if (!TextUtils.isEmpty(res.getPrjName())&&res.getPrjName().equals(Cache.getInstance().load(Cache.PRJ_NAME,""))) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("name", res.getName());
                item.put("force1", res.getForce1() + "");
                item.put("force2", res.getForce2() + "");
                item.put("force3", res.getForce3() + "");
                item.put("id", res.getId());
                items.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position > 0) {
            new AlertDialog.Builder(getActivity()).setMessage("删除该行数据吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long dbId = Long.parseLong(items.remove(position).get("id").toString());
                            DB.deleteResult(dbId);
                            adapter.notifyDataSetChanged();
                        }
                    }).setNegativeButton("取消", null).show();
        }
    }
}
