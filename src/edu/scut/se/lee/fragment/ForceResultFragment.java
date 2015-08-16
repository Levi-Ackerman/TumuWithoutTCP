package edu.scut.se.lee.fragment;

import android.view.View;
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

/**
 * Created by jsonlee on 7/26/15.
 */
public class ForceResultFragment extends BaseFragment implements View.OnClickListener {
    private SimpleAdapter adapter;
    @ViewInject(id = R.id.lvResult)
    private ListView mLVResult;
    @ViewInject(id = R.id.btn_load_result,click = "onClick")
    private Button btnLoadResult;
    private List<Map<String,String>> items;
    @Override
    public int getRootRes() {
        return R.layout.fragment_force_result;
    }

    @Override
    public void initData() {
        items = new ArrayList<Map<String, String>>();
        initItems();
        adapter = new SimpleAdapter(App.getInstance(),items,R.layout.item_result,new String[]{"name","frequency","result1","result2"},new int[]{R.id.tv0,R.id.tv1,R.id.tv2,R.id.tv3});
        mLVResult.setAdapter(adapter);
    }

    private void initItems(){
        items.clear();
        Map<String ,String > item = new HashMap<String, String>();
        item.put("name","名字");
        item.put("frequency","频率");
        item.put("result1","公式1");
        item.put("result2","公式2");
        items.add(item);
    }

    @Override
    public void onClick(View v) {
        initItems();
        List<DB.Result> ltRes = DB.getResults();
        for (DB.Result res : ltRes) {
            Map<String,String> item =new HashMap<String, String>();
            item.put("name", res.getName());
            item.put("frequency", res.getFrequency()+"");
            item.put("result1", res.getResult1()+"");
            item.put("result2",res.getResult2()+"");
            items.add(item);
        }
        adapter.notifyDataSetChanged();
    }
}
