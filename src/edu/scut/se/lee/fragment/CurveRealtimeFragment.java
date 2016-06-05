package edu.scut.se.lee.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.annotation.view.ViewInject;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.scut.se.lee.R;
import edu.scut.se.lee.util.DB;
import edu.scut.se.lee.util.Data;
import edu.scut.se.lee.util.FFT;
import edu.scut.se.lee.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.view.MenuItem;
import android.widget.EditText;


public class CurveRealtimeFragment extends BaseFragment implements
        OnCheckedChangeListener, OnClickListener {

    private static final double WUCHA = 0.1;
    @ViewInject(id = R.id.lay_curve_root)
    private RelativeLayout dynamic_chart_line_layout;

    private String title = "";

    @ViewInject(id = R.id.sw_curve_start)
    private Switch sw_start;
    @ViewInject(id = R.id.btn_curve_pinyu, click = "onClick")
    private Button btnPinyu;
    @ViewInject(id = R.id.btn_curve_shiyu, click = "onClick")
    private Button btn_shiyu;
    @ViewInject(id = R.id.button_jisuansuoli, click = "onClick")
    private Button btnJisuan;

    @ViewInject(id = R.id.btn_set_freq, click = "onClick")
    private Button btnSetFreq;
    @ViewInject(id = R.id.et_set_freq)
    private EditText etSetFreq;

    @ViewInject(id = R.id.btn_import, click = "onClick")
    private Button btnSave;
    @ViewInject(id = R.id.et_auto_run_time)
    private EditText etAutoRunTime;

    @ViewInject(id = R.id.btn_import, click = "onClick")
    private Button btnImport;

    @ViewInject(id = R.id.baseFreq, click = "onClick")
    Button basefreq;

    @ViewInject(id = R.id.baseFreqNum)
    TextView tvFreqNum;

    @ViewInject(id = R.id.suoliNum)
    TextView tvSuoliNum;

    // 用于存放每条折线的点数据
    private XYSeries line1;//linex,liney;
    List<XYSeries> points;
    // 用于存放所有需要绘制的XYSeries
    private XYMultipleSeriesDataset mDataset;
    // 用于存放每条折线的风格
    private XYSeriesRenderer renderer1;//rendererx,renderery;
    private List<XYSeriesRenderer> renderer2;
    // 用于存放所有需要绘制的折线的风格
    private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
    private GraphicalView chart;

    private MyListener listener;
    private SensorManager sensors;
    private Sensor sensor;

    boolean isAvailable;

    private XYSeries line2;

    static final int SelectPointNum = 10;

    //tvToast = (TextView) findViewById(R.id.toast);

    @Override
    public int getRootRes() {
        // TODO Auto-generated method stub
        return R.layout.fragment_curve_realtime;
    }

    private int autoSec = -1;

    @Override
    public void initData() {
        // TODO Auto-generated method stub
        //改动：初始化控件switch
        sw_start = (Switch) rootView.findViewById(R.id.sw_curve_start);

        time_elems = new ArrayList<Elem>();
        initAcceler();
        initChart();
        tvFreqNum.setText("");
    }

    long startMillin;
    // FileWriter fileWriter;
    private String text;
    int index = 0;
    double lastSec = 0;
    double lastX = 0, lastY = 0;
    List<Elem> time_elems;

    class Elem {
        public Elem(long milliSec, float acce) {
            this.milliSec = milliSec;
            this.acce = acce;
        }

        public long milliSec;
        public float acce;
    }

    boolean isFirst = true;

    class MyListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isFirst) {
                isFirst = false;
                startMillin = System.currentTimeMillis();
            }
            long delay = (System.currentTimeMillis() - startMillin);
            time_elems.add(new Elem(delay, event.values[2]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    }

    private void initAcceler() {
        listener = new MyListener();
        // cb_start = (CheckBox) findViewById(R.id.cb_start);
        sw_start.setOnCheckedChangeListener(this);
        // db = FinalDb.create(this);
        sensors = (SensorManager) getActivity().getSystemService(
                Context.SENSOR_SERVICE);
        sensor = sensors.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    private void initChart() {
        // 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
        // XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
        line1 = new XYSeries("");
        line2 = new XYSeries("");
        points = new ArrayList<XYSeries>();
        initLine();
        // line2 = new XYSeries("折线2");
        renderer1 = new XYSeriesRenderer();
        renderer2 = new ArrayList<XYSeriesRenderer>();
        mDataset = new XYMultipleSeriesDataset();
        mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

        initRenderer(renderer1, Color.RED, PointStyle.POINT, true, 4);

        // 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
        mDataset.addSeries(0, line1);
        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
        // mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);

        // 配置chart参数
        setChartSettings(mXYMultipleSeriesRenderer, "时间(s)", "加速度(m/s^2)",
                -1, 1, Color.BLUE, Color.WHITE);

        // 通过该函数获取到一个View 对象
        chart = ChartFactory.getLineChartView(getActivity(), mDataset,
                mXYMultipleSeriesRenderer);

        chart.setOnClickListener(null);
        // 将该View 对象添加到layout中。
        View towBtns = dynamic_chart_line_layout.findViewById(R.id.twoBtns);
        dynamic_chart_line_layout.removeView(towBtns);
        dynamic_chart_line_layout.addView(chart, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        dynamic_chart_line_layout.addView(towBtns, towBtns.getLayoutParams());
        chart.setBackgroundColor(Color.WHITE);
    }

    class Point {
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (Double.compare(point.x, x) != 0) return false;
            return Double.compare(point.y, y) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        double x;
        double y;
    }

    private List<Point> maxValuesPinyu = new ArrayList<Point>();
    private List<Point> maxValuesShiyu = new ArrayList<Point>();
    private final int MAX_VALUES_COUNT = 500;
    private OnClickListener chartOnClickListenerInPinyu = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            GraphicalView gv = (GraphicalView) v;
            // 将view转换为可以监听的GraphicalView
            final SeriesSelection ss = gv.getCurrentSeriesAndPoint();
            // 获得被点击的系列和点
            if (ss == null)
                return;
            int index = ss.getPointIndex();
            for (int i = 0; i < SelectPointNum; i++) {
                if (index == 0 || index == line2.getItemCount() - 1) {
                    index = -1;
                    break;
                } else {
                    if (line2.getY(index) < line2.getY(index + 1))
                        index++;
                    else if (line2.getY(index) < line2.getY(index - 1)) {
                        index--;
                    } else {
                        break;
                    }
                }
            }
            if (index != -1 && line2.getY(index) > line2.getY(index - 1) && line2.getY(index) > line2.getY(index + 1)) {
                Point point = new Point(line2.getX(index), line2.getY(index));
                if (maxValuesPinyu.size() >= MAX_VALUES_COUNT) {
                    showMsg("已经选了" + MAX_VALUES_COUNT + "个点了,请勿再多选");
                    return;
                }
                maxValuesPinyu.add(point);
                XYSeries series = new XYSeries("");
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                initRenderer(renderer, Color.BLUE, PointStyle.CIRCLE, true, 0);
                renderer.setPointStrokeWidth(8);
                series.add(point.x, point.y);
                mDataset.addSeries(series);
                mXYMultipleSeriesRenderer.addSeriesRenderer(renderer);
                Log.i("lee", "点击了" + point.x + "," + point.y);
                chart.invalidate();

            } else {
                showMsg(String.format("附近没有极大值，已经选中了%d个极大值", maxValuesPinyu.size()));
            }
        }
    };
    private OnClickListener chartOnClickListenerInShiyu = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            GraphicalView gv = (GraphicalView) v;
            // 将view转换为可以监听的GraphicalView
            final SeriesSelection ss = gv.getCurrentSeriesAndPoint();
            // 获得被点击的系列和点
            if (ss == null)
                return;
            int index = ss.getPointIndex();
            for (int i = 0; i < SelectPointNum; i++) {
                if (index == 0 || index == line1.getItemCount() - 1) {
                    index = -1;
                    break;
                } else {
                    if (line1.getY(index) < line1.getY(index + 1))
                        index++;
                    else if (line1.getY(index) < line1.getY(index - 1)) {
                        index--;
                    } else {
                        break;
                    }
                }
            }
            if (index != -1 && line1.getY(index) > line1.getY(index - 1) && line1.getY(index) > line1.getY(index + 1)) {
                Point point = new Point(line1.getX(index), line1.getY(index));
                if (maxValuesShiyu.size() >= 2) {
                    showMsg("已经选了" + 2 + "个点了,请勿再多选");
                    return;
                }
                maxValuesShiyu.add(point);
                XYSeries series = new XYSeries("");
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                initRenderer(renderer, Color.BLUE, PointStyle.CIRCLE, true, 0);
                renderer.setPointStrokeWidth(8);
                series.add(point.x, point.y);
                mDataset.addSeries(series);
                mXYMultipleSeriesRenderer.addSeriesRenderer(renderer);
                Log.i("lee", "点击了" + point.x + "," + point.y);
                if (maxValuesShiyu.size() == 2) {
                    double large = Math.max(maxValuesShiyu.get(0).x, maxValuesShiyu.get(1).x);
                    double min = Math.min(maxValuesShiyu.get(0).x, maxValuesShiyu.get(1).x);
                    for (int i = line1.getItemCount() - 1; line1.getX(i) > large; i--) {
                        line1.remove(i);
                    }
                    while (line1.getX(0) < min) {
                        line1.remove(0);
                    }
                }
                chart.invalidate();
            } else {
                showMsg(String.format("附近没有极大值，已经选中了%d个极大值", maxValuesShiyu.size()));
            }
        }
    };

    private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
                                          PointStyle style, boolean fill, int width) {
        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        renderer.setColor(color);
        renderer.setPointStyle(style);
        renderer.setFillPoints(fill);
        renderer.setLineWidth(width);
        return renderer;
    }

    double time_cur_min_x = 0, time_cur_max_x = 5;
    double time_cur_min_y = 0, time_cur_max_y = 5;

    protected void setChartSettings(
            XYMultipleSeriesRenderer mXYMultipleSeriesRenderer, String xTitle,
            String yTitle, double yMin, double yMax, int axesColor,
            int labelsColor) {
        // 有关对图表的渲染可参看api文档
        mXYMultipleSeriesRenderer.setChartTitle(title);
        mXYMultipleSeriesRenderer.setXTitle(xTitle);
        mXYMultipleSeriesRenderer.setYTitle(yTitle);
        mXYMultipleSeriesRenderer.setAxisTitleTextSize(30);
        mXYMultipleSeriesRenderer.setChartTitleTextSize(50);
        mXYMultipleSeriesRenderer.setLabelsTextSize(15);
        mXYMultipleSeriesRenderer.setXAxisMin(time_cur_min_x);
        mXYMultipleSeriesRenderer.setXAxisMax(time_cur_max_x);
        mXYMultipleSeriesRenderer.setYAxisMin(yMin);
        mXYMultipleSeriesRenderer.setYAxisMax(yMax);
        mXYMultipleSeriesRenderer.setAxesColor(axesColor);
        mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
        mXYMultipleSeriesRenderer.setBackgroundColor(Color.WHITE);
        mXYMultipleSeriesRenderer.setShowGrid(false);
        mXYMultipleSeriesRenderer.setGridColor(Color.GRAY);
        mXYMultipleSeriesRenderer.setXLabels(20);
        mXYMultipleSeriesRenderer.setYLabels(20);
        mXYMultipleSeriesRenderer.setXTitle(xTitle);
        mXYMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
        mXYMultipleSeriesRenderer.setPointSize((float) 5);
        mXYMultipleSeriesRenderer.setShowLegend(true);
        mXYMultipleSeriesRenderer.setLegendTextSize(20);
        mXYMultipleSeriesRenderer.setClickEnabled(true);
        mXYMultipleSeriesRenderer.setSelectableBuffer(30);
        mXYMultipleSeriesRenderer.setShowAxes(true);

        // mXYMultipleSeriesRenderer.setBackgroundColor(Color.WHITE);
    }

    static final int MSG_CLOSE_SWITCH = 0;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_SWITCH:
                    sw_start.setChecked(false);
                    break;
            }
        }
    };

    private int Frequence = 50;

    class RefreshSeriesTask extends TimerTask {
        public void run() {
            Log.i("lee.","采样频率"+Frequence);
            if (autoSec > 0 && line1.getItemCount() > Frequence * autoSec) {
                handler.sendEmptyMessage(MSG_CLOSE_SWITCH);
                return;
            }
            int count = time_elems.size();
            if (count == 0)
                return;
            if (index == 0) {
                index++;
                lastX = time_elems.get(0).milliSec;
                lastY = time_elems.get(0).acce;
                line1.add(lastX * 0.001, lastY);
                time_elems.remove(0);
            } else {
                for (int i = 0; i < count; i++) {
                    long sec = time_elems.get(0).milliSec;
                    if (sec < 1000 / Frequence * index) {
                        lastX = sec;
                        lastY = time_elems.get(0).acce;
                    } else if (sec == 1000 / Frequence * index) {
                        lastX = sec;
                        lastY = time_elems.get(0).acce;
                        line1.add(lastX * 0.001, lastY);
                        index++;
                    } else {
                        float y = time_elems.get(0).acce;
                        lastY = (y - lastY) / (sec - lastX)
                                * (1000 / Frequence * index - lastX) + lastY;
                        lastX = 1000 / Frequence * index;
                        line1.add(lastX * 0.001, lastY);
                        index++;
                    }

                    time_elems.remove(0);
                }

            }

            double maxtemp = line1.getMaxX();
            double mintemp = maxtemp - time_cur_max_x + time_cur_min_x;

            if (maxtemp >= time_cur_max_x || maxtemp <= time_cur_min_x) {
                // 不在视野里，就要移动窗口
                mXYMultipleSeriesRenderer.setXAxisMax(maxtemp);
                mXYMultipleSeriesRenderer.setXAxisMin(mintemp);
                time_cur_max_x = maxtemp;
                time_cur_min_x = mintemp;
            }
            mXYMultipleSeriesRenderer.setYAxisMax(line1.getMaxY() * 1.1);
            mXYMultipleSeriesRenderer.setYAxisMin(line1.getMinY() * 1.1);
            chart.postInvalidate();
        }
    }

    /**
     * 刷新间隔
     */
    private long refreshDelay = 100;
    Timer timer;

    // private void refreshChart() {
    // }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

        if (isChecked) {
            if (mDataset.getSeriesAt(0) != line1) {
                mDataset.clear();
                mDataset.addSeries(0, line1);
                maxValuesPinyu.clear();
                mXYMultipleSeriesRenderer.removeAllRenderers();
                mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
                chart.invalidate();
            }
            disableAllBtnsAndInput();
            try {
                if (TextUtils.isEmpty(etAutoRunTime.getText()))
                    autoSec = -1;
                else
                    autoSec = Integer.parseInt(etAutoRunTime.getText().toString());
            } catch (Exception e) {
                autoSec = -1;
                showMsg("请填写整数");
                buttonView.setChecked(false);
                onCheckedChanged(buttonView, false);
                return;
            }
            buttonView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ready();
                }
            }, 0);
        } else if (isAvailable) {
            enableAllBtnsAndInput();
            timer.cancel();
            sensors.unregisterListener(listener);
            content = "";
            for (int i = 0; i < line1.getItemCount(); i++) {
                content += line1.getX(i) + "\t" + line1.getY(i)
                        + "\n";
            }
            if (Util.saveFileInPrjDir(edu.scut.se.lee.util.Data.name + ".data", content))
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
                        .show();
            else
                Util.showToast("保存失败");
        }
    }

    public void ready() {
        isAvailable = sensors.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);
        if (!isAvailable) {
            new AlertDialog.Builder(getActivity()).setMessage("不支持加速度传感器")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    getActivity().finish();
                                }
                            });
        } else {
            time_elems.clear();
            index = 0;
            lastX = lastY = 0;
            isFirst = true;
            ;
            initLine();
            timer = new Timer();
            timer.schedule(new RefreshSeriesTask(), 10, refreshDelay);

        }
    }

    private void disableAllBtnsAndInput() {
        btn_shiyu.setEnabled(false);
        btnJisuan.setEnabled(false);
        btnPinyu.setEnabled(false);
        btnSave.setEnabled(false);
        btnSetFreq.setEnabled(false);
        etAutoRunTime.setEnabled(false);
        etSetFreq.setEnabled(false);
    }

    private void enableAllBtnsAndInput() {
        btn_shiyu.setEnabled(true);
        btnJisuan.setEnabled(true);
        btnPinyu.setEnabled(true);
        btnSave.setEnabled(true);
        btnSetFreq.setEnabled(true);
        etAutoRunTime.setEnabled(true);
        etSetFreq.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initLine() {
        if (line1.getItemCount() > 0) {
            line1.clear();
        }
        if (line2.getItemCount() > 0) {
            line2.clear();
        }
    }

    String content;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_jisuansuoli:
                if (jisuanBaseFreq()) {
                    if (maxValuesPinyu.size() < 2) {
                        showMsg("还没有选够2个极大值");
                    } else {
//                        tvSuoliNum.setText(String.format("%.2f", Data.getForce1()));
                        DB.putResult(new DB.Result(Data.name, Data.lineLength, Data.getForce1(), Data.getForce2(), Data.getForce3()));
//                        saveToFile();
                    }
                }
                break;
            case R.id.baseFreq:
                jisuanBaseFreq();
                break;
            case R.id.btn_import://还原数据
                final EditText et = new EditText(getActivity());
                et.setText("data.txt");
                new AlertDialog.Builder(getActivity()).setTitle("输入数据名字").setView(et).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] data = Util.loadFileLinesInPrjDir(et.getText().toString().trim());
                        if (data == null) {
                            showMsg("不存在该数据");
                            return;
                        }
                        initChart();
                        int count = data.length;
                        if (count > FFT.POINT_COUNT) {
                            count = FFT.POINT_COUNT;
                        }
                        initLine();
                        for (int i = 0; i < count; i++) {
                            float f = Float.parseFloat(data[i]);
                            Elem pt = new Elem(1000 / Frequence * i, f);
                            time_elems.add(pt);
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                Runnable run = new RefreshSeriesTask();
                                run.run();
                                run.run();
                                System.out.println("刷新完成");
                            }
                        }.start();
                    }
                }).setNegativeButton("cancel", null).show();
                break;
            case R.id.btn_curve_pinyu:
                mXYMultipleSeriesRenderer.setXTitle("频率(Hz)");
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
//						if(line2.getItemCount()==0) {
                        int size = line1.getItemCount();
                        double[] arr = new double[FFT.POINT_COUNT];
                        for (int i = 0; i < arr.length; i++) {
                            if (i < size)
                                arr[i] = line1.getY(i);
                            else
                                arr[i] = 0;
                        }
                        FFT fft = new FFT(arr);
                        int count = fft.count;
                        double[] ys = fft.result;
                        line2.add(0, 0);
                        for (int i = 1; i < count; i++) {
                            double x = 1.0 * Frequence / 2 * i / count;
                            line2.add(x, ys[i - 1]);
                            Log.i("lee", "频域点" + x + "," + ys[i - 1]);
                        }
//						}
                        setMaxMin(line2.getMinX() - 0.1 * (line2.getMaxX() - line2.getMinX()), line2.getMinY() - 0.1 * (line2.getMaxY() - line2.getMinY()), line2.getMaxX() + 0.1 * (line2.getMaxX() - line2.getMinX()), line2.getMaxY() + 0.1 * (line2.getMaxY() - line2.getMinY()));
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        mDataset.clear();
                        mDataset.addSeries(0, line2);
                        for (XYSeries point : points) {
                            mDataset.addSeries(point);
                        }
                        mXYMultipleSeriesRenderer.removeAllRenderers();
                        mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
                        for (XYSeriesRenderer xySeriesRenderer : renderer2) {
                            mXYMultipleSeriesRenderer.addSeriesRenderer(xySeriesRenderer);
                        }
                        chart.postInvalidate();
                        chart.setOnClickListener(chartOnClickListenerInPinyu);
                    }
                }.execute((Void) null);
                break;
            case R.id.btn_curve_shiyu:
                mXYMultipleSeriesRenderer.setXTitle("时间(s)");
                mDataset.clear();
                mDataset.addSeries(0, line1);
                mXYMultipleSeriesRenderer.removeAllRenderers();
                mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
                setMaxMin(line1.getMinX() - 0.1 * (line1.getMaxX() - line1.getMinX()), line1.getMinY() - 0.1 * (line1.getMaxY() - line1.getMinY()), line1.getMaxX() + 0.1 * (line1.getMaxX() - line1.getMinX()), line1.getMaxY() + 0.1 * (line1.getMaxY() - line1.getMinY()));
                chart.postInvalidate();
                chart.setOnClickListener(chartOnClickListenerInShiyu);
                break;
            case R.id.btn_set_freq:
                if (TextUtils.isEmpty(etSetFreq.getText()))
                    showMsg("不能为空");
                else {
                    try {
                        Frequence = Integer.parseInt(etSetFreq.getText().toString());
                    } catch (Exception e) {
                        Frequence = 50;
                        etSetFreq.setText("50");
                        showMsg("请在左边输入数字");
                    }
                }
                break;
        }
    }

    public void saveToFile() {
        content = "";
        for (int i = 0; i < line1.getItemCount(); i++) {
            content += line1.getX(i) + "\t" + line1.getY(i)
                    + "\n";
        }
        final EditText editText = new EditText(getActivity());
        editText.setHint("输入文件名");
        new AlertDialog.Builder(getActivity()).setTitle("保存数据").setView(editText).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString().trim();
                if (name.equals("")) {
                    Util.showToast("请输入文件名");
                    return;
                }
                if (Util.saveFileInPrjDir(name + ".data", content))
                    Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
                            .show();
                else
                    Util.showToast("保存失败");
            }
        }).setNegativeButton("取消", null).show();
    }

    public boolean jisuanBaseFreq() {
        if (maxValuesPinyu.size() >= 2) {
            Collections.sort(maxValuesPinyu, new Comparator<Point>() {
                @Override
                public int compare(Point lhs, Point rhs) {
                    return (int) (lhs.x - rhs.x);
                }
            });
            double minFreq = Double.MAX_VALUE;
            for (int i = 1; i < maxValuesPinyu.size(); i++) {
                double temp = (maxValuesPinyu.get(i).x - maxValuesPinyu.get(i - 1).x);
                if (temp < minFreq) {
                    minFreq = temp;
                }
            }
            tvFreqNum.setText(String.format("%.5f", minFreq));
            List<Data.JiePin> jiePins = new ArrayList<Data.JiePin>();
            int i = 1;
            for (Point point : maxValuesPinyu) {
                double n = point.x / minFreq;
                Log.i("lee.", "第" + (i++) + "个数是第" + n + "阶");
                double tempN = Math.abs(Math.round(n) - n);//四舍五入后值与n的差，控制误差
                if (tempN < WUCHA) {
                    jiePins.add(new Data.JiePin((int) Math.round(n), point.x));
                    Log.i("lee.", "误差为" + tempN + "，留下");
                } else {
                    Log.i("lee.", "误差为" + tempN + ",太大了，舍去");
                }
            }
            Data.jiePins = jiePins.toArray(new Data.JiePin[jiePins.size()]);
        } else {
            showMsg("点数不足2");
            return false;
        }
        return true;
    }

    private void setMaxMin(double xmin, double ymin, double xmax, double ymax) {
        mXYMultipleSeriesRenderer.setXAxisMin(xmin);
        mXYMultipleSeriesRenderer.setXAxisMax(xmax);
        mXYMultipleSeriesRenderer.setYAxisMin(ymin);
        mXYMultipleSeriesRenderer.setYAxisMax(ymax);
    }
}
