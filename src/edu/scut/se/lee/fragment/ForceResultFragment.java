package edu.scut.se.lee.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
        adapter = new SimpleAdapter(App.getInstance(), items, R.layout.item_result, new String[]{"name", "midu", "length", "freq", "force"}, new int[]{R.id.tv0, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4});
        mLVResult.setAdapter(adapter);
        load();
    }

    private void initItems() {
        items.clear();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "索名");
        item.put("midu", "线密度(kg/m)");
        item.put("length", "索长(m)");
        item.put("freq", "基频(Hz)");
        item.put("force", "索力(kN)");
        items.add(item);
    }

    @Override
    public void onClick(View v) {
//        load();
        StringBuilder builder = new StringBuilder("索名\t线密度(kg/m)\t索长(m)\t基频(Hz)\t索力(kN)\t\n");
        List<DB.Result> ltRes = DB.getResults();
        for (DB.Result ltRe : ltRes) {
            //第一行是表头
            builder.append(ltRe.getName()).append("\t")
                    .append(ltRe.getMidu()).append("\t")
                    .append(ltRe.getLength()).append("\t")
                    .append(ltRe.getFreq()).append("\t")
                    .append(ltRe.getForce()).append("\t\n");
        }
        Util.saveFileInPrjDir(Data.name+"索力结果"+".txt",builder.toString());
        showMsg("保存成功");
    }

    public void load() {
        initItems();
        List<DB.Result> ltRes = DB.getResults();
        for (DB.Result res : ltRes) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", res.getName());
            item.put("length", res.getLength() + "");
            item.put("midu", res.getMidu() + "");
            item.put("freq", res.getFreq() + "");
            item.put("force", res.getForce() + "");
            item.put("id", res.getId());
            items.add(item);
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
