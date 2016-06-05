package edu.scut.se.lee;

import edu.scut.se.lee.fragment.BaseFragment;
import edu.scut.se.lee.fragment.CurveRealtimeFragment;
import edu.scut.se.lee.fragment.ForceResultFragment;
import edu.scut.se.lee.fragment.InputFragment;
import edu.scut.se.lee.fragment.ProjectManagerFragment;
import edu.scut.se.lee.util.Cache;
import edu.scut.se.lee.util.Data;
import edu.scut.se.lee.util.Util;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;

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

        test();
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        initAlertDlg();
    }

    private void test() {
        Data.midu = 61.39;
        Data.lineLength = 128.05;
        Data.ei = 5430000;
        Data.jiePins = new Data.JiePin[2];
        Data.jiePins[0] = new Data.JiePin(1,0.957);
        Data.jiePins[1] = new Data.JiePin(2,2.914);
//        Data.jiePins[2] = new Data.JiePin(3,2.871);
//        Data.jiePins[3] = new Data.JiePin(4,3.828);
        Log.i("索力",Data.getForce1()+","+ Data.getForce2()+","+Data.getForce3());
    }

    private void initAlertDlg() {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        BaseFragment fragment = null;
        switch (position) {
            case 1:
                fragment = new InputFragment();
                break;
            case 2:
                fragment = new CurveRealtimeFragment();
                break;
            case 3:
                fragment = new ForceResultFragment();
                break;
            case 0:
                fragment = new ProjectManagerFragment();
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment)
                .commit();
        Intent intent = new Intent("drawer_item_selected");
        intent.putExtra("index",position);
        sendBroadcast(intent);
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
