package edu.scut.se.lee;

import edu.scut.se.lee.fragment.ArrayDisplayFragment;
import edu.scut.se.lee.fragment.BaseFragment;
import edu.scut.se.lee.fragment.CurveRealtimeFragment;
import edu.scut.se.lee.fragment.FFTAnalysisFragment;
import edu.scut.se.lee.fragment.ForceComputeFragment;
import edu.scut.se.lee.fragment.ForceResultFragment;
import edu.scut.se.lee.fragment.InputFragment;
import edu.scut.se.lee.fragment.ParamsDisplayFragment;
import edu.scut.se.lee.util.Cache;
import edu.scut.se.lee.util.Util;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends BaseActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        initAlertDlg();
    }

    private void initAlertDlg() {
        final EditText etName = new EditText(this);
        etName.setHint("输入项目名称");
        etName.setText(Cache.getInstance().load(Cache.PRJ_NAME,""));
        new AlertDialog.Builder(this).setTitle("项目名称")
                .setView(etName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString().trim();
                        if (name == null||name.equals("")) {
                            Util.showToast("项目名字不能为空");
                            finish();
                            return ;
                        }
                        Cache.getInstance().save(Cache.PRJ_NAME,name);
                        File file = new File(Util.getPrjDir());
                        if (!file.exists() || !file.isDirectory()) {
                            file.mkdirs();
                        }
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        BaseFragment fragment = null;
        switch (position) {
            case 0:
                fragment = new InputFragment();
                break;
            case 1:
                fragment = new ParamsDisplayFragment();
                break;
            case 2:
                fragment = new CurveRealtimeFragment();
                break;
            case 3:
                fragment = new ArrayDisplayFragment();
                break;
            case 4:
                fragment = new FFTAnalysisFragment();
                break;
            case 5:
                fragment = new ForceComputeFragment();
                break;
            case 6:
                fragment = new ForceResultFragment();
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .commit();
    }

    int[] titleRes = {R.string.title_section0_input,
            R.string.title_section1_display_params,
            R.string.title_section2_realtime_curve,
            R.string.title_section3_array};

    public void onSectionAttached(int number) {
        mTitle = getString(titleRes[number - 1]);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
