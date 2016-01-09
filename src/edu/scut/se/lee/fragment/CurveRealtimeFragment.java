package edu.scut.se.lee.fragment;

import java.util.ArrayList;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import android.view.MenuItem;
import android.widget.EditText;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;


public class CurveRealtimeFragment extends BaseFragment implements
		OnCheckedChangeListener, OnClickListener {

	@ViewInject(id = R.id.lay_curve_root)
	private RelativeLayout dynamic_chart_line_layout;

    private String title = "加速度";

    @ViewInject(id = R.id.sw_curve_start)
    private Switch sw_start;
    @ViewInject(id = R.id.btn_curve_pinyu, click = "onClick")
	private Button btnPinyu;
    @ViewInject(id = R.id.btn_curve_shiyu,click = "onClick")
    private Button btn_shiyu;
    @ViewInject(id = R.id.button_jisuan, click = "onClick")
    private Button  btnJisuan;

	@ViewInject(id = R.id.btn_set_freq,click="onClick")
	private Button btnSetFreq;
	@ViewInject(id = R.id.et_set_freq)
	private EditText etSetFreq;

	@ViewInject(id = R.id.btn_save,click = "onClick")
	private Button btnSave;
	@ViewInject(id=R.id.et_auto_run_time)
	private EditText etAutoRunTime;

	// 用于存放每条折线的点数据
	private XYSeries line1;
	// 用于存放所有需要绘制的XYSeries
	private XYMultipleSeriesDataset mDataset;
	// 用于存放每条折线的风格
	private XYSeriesRenderer renderer1;
	// 用于存放所有需要绘制的折线的风格
	private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
	private GraphicalView chart;

	private MyListener listener;
	private SensorManager sensors;
	private Sensor sensor;

    boolean isAvailable;
    private IoSession ioSession;
    private NioSocketConnector connector;
    private IoHandler iohandler ;
    //EditText etContent, etPort;
    private String strIP;
    private int port;
    boolean isOpening = false;
	private XYSeries line2;

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
        sw_start = (Switch)rootView.findViewById(R.id.sw_curve_start);

		time_datas = new ArrayList<Data>();
		initAcceler();
		initChart();
	}

	long startMillin;
	// FileWriter fileWriter;
	private String text;
	int index = 0;
	double lastSec = 0;
	double lastX = 0, lastY = 0;
	List<Data> time_datas;

	class Data {
		public Data(long milliSec, float acce) {
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
			// db.save(new AccelerateData(System.currentTimeMillis(),
			// event.values[2]));
			// long tm = (System.currentTimeMillis() - startMillin);
			if (isFirst) {
				isFirst = false;
				startMillin = System.currentTimeMillis();
			}
			long delay = (System.currentTimeMillis() - startMillin);
			time_datas.add(new Data(delay, event.values[2]));

			// text = "" + event.values[2];
			// try {
			// fileWriter.write(delay + "\t" + text + "\n");
			// textView.setText(text);

			// line1.add(0.001 * delay, event.values[2]);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
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
		line1 = new XYSeries("加速度曲线");
		line2 = new XYSeries("频域曲线");
		initLine();
		// line2 = new XYSeries("折线2");
		renderer1 = new XYSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();
		mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

		// 对XYSeries和XYSeriesRenderer的对象的参数赋值
		// initLine(line1);
		// initLine(line2);
		initRenderer(renderer1, Color.RED, PointStyle.POINT, true,4);
		// initRenderer(renderer2, Color.CYAN, PointStyle.TRIANGLE, true);

		// 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
		mDataset.addSeries(line1);
		// mDataset.addSeries(line2);
		mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
		// mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);

		// 配置chart参数
		setChartSettings(mXYMultipleSeriesRenderer, "时间(s)", "加速度(m/s^2)",
				-1, 1, Color.BLUE, Color.WHITE);

		// 通过该函数获取到一个View 对象
		chart = ChartFactory.getLineChartView(getActivity(), mDataset,
				mXYMultipleSeriesRenderer);

		chart.setBackgroundColor(Color.GRAY);
		chart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GraphicalView gv = (GraphicalView) v;
				// 将view转换为可以监听的GraphicalView
				final SeriesSelection ss = gv.getCurrentSeriesAndPoint();
				// 获得被点击的系列和点
				if (ss == null)
					return;
				// double[] point = new double[] { ss.getXValue(), ss.getValue()
				// };
				// 获得当前被点击点的X位置和Y数值
				// final double[] dest = chart.toScreenPoint(point);
				// 获得当前被点击点的坐标位置

				new AlertDialog.Builder(getActivity())
						.setMessage(
								"删除(" + ss.getXValue() + "," + ss.getValue()
										+ ")点?")
						.setNegativeButton("取消", null)
						.setPositiveButton("删除",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
//										Toast.makeText(getActivity(),
//												ss.getPointIndex() + "",
//												Toast.LENGTH_SHORT).show();
										line1.remove(ss.getPointIndex());
										chart.postInvalidate();
									}
								}).show();
				// Toast.makeText(getActivity(),
				// "点击了(" + ss.getXValue() + "," + ss.getValue() + ")点", 1)
				// .show();
			}
		});
		// 将该View 对象添加到layout中。
		dynamic_chart_line_layout.addView(chart, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		chart.setBackgroundColor(Color.WHITE);
	}

	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
			PointStyle style, boolean fill,int width) {
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
		// mXYMultipleSeriesRenderer.setBackgroundColor(Color.WHITE);
	}

	static final int MSG_CLOSE_SWITCH = 0;
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case MSG_CLOSE_SWITCH:
					sw_start.setChecked(false);
					break;
			}
		}
	};

	private int Frequence = 50;
	class RefreshSeriesTask extends TimerTask {
		public void run() {
			// initLine(line1);
			// initLine(line2);
			// System.out.println("refreshing");
			// chart.scrollBy(10, 0);
			if (line1.getItemCount()>1000/50*autoSec){
				handler.sendEmptyMessage(MSG_CLOSE_SWITCH);
				return ;
			}
			int count = time_datas.size();
              String value;
             byte[] data;
			if (count == 0)
				return;
			if (index == 0) {
				index++;
				lastX = time_datas.get(0).milliSec;
				lastY = time_datas.get(0).acce;
				line1.add(lastX * 0.001, lastY);
				time_datas.remove(0);
			} else {
				for (int i = 0; i < count; i++) {
					long sec = time_datas.get(0).milliSec;
					if (sec < 20 * index) {
						lastX = sec;
						lastY = time_datas.get(0).acce;
					} else if (sec == 20 * index) {
						lastX = sec;
						lastY = time_datas.get(0).acce;
                        line1.add(lastX * 0.001, lastY);
                        index++;
					} else {
						float y = time_datas.get(0).acce;
						lastY = (y - lastY) / (sec - lastX)
								* (20 * index - lastX) + lastY;
						lastX = 20 * index;
						line1.add(lastX * 0.001, lastY);
						index++;
					}

					time_datas.remove(0);
				}

			}

			double maxtemp = line1.getMaxX();
			double mintemp = maxtemp - time_cur_max_x + time_cur_min_x;

			if (maxtemp >= time_cur_max_x || maxtemp <= mintemp) {
				// 不在视野里，就要移动窗口
				mXYMultipleSeriesRenderer.setXAxisMax(maxtemp);
				mXYMultipleSeriesRenderer.setXAxisMin(mintemp);
				time_cur_max_x = maxtemp;
				time_cur_min_x = mintemp;
			}
			mXYMultipleSeriesRenderer.setYAxisMax(line1.getMaxY());
			mXYMultipleSeriesRenderer.setYAxisMin(line1.getMinY());
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
			try {
				if (TextUtils.isEmpty(etAutoRunTime.getText()))
					autoSec = -1;
				else
					autoSec = Integer.parseInt(etAutoRunTime.getText().toString());
			}catch(Exception e){
				autoSec = -1;
				showMsg("请填写整数");
				buttonView.setChecked(false);
				return ;
			}
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
				time_datas.clear();
				index = 0;
				lastX = lastY = 0;
				isFirst = true;
				// time_datas.add(new Data(0, 0));
				// index++;
				initLine();
				timer = new Timer();
				timer.schedule(new RefreshSeriesTask(), 10, refreshDelay);

			}
		} else if (isAvailable) {
			timer.cancel();
			sensors.unregisterListener(listener);
			// try {
			// fileWriter.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
	}

  /*  public void initData1() {
        connector = new NioSocketConnector(); // 创建连接客户端
        connector.setConnectTimeoutMillis(10000); // 设置连接超时
        // 添加处理器，主要负责收包
        connector.setHandler(this);
        strIP = etIP.getText().toString().trim();
//        strContent = etContent.getText().toString().trim();
        port = Integer.parseInt(etPort.getText().toString().trim());
        setConfig("ip", strIP);
        setConfig("port", port + "");
    }
    */
    //将数据保存在历史记录中
    void setConfig(String key, String value) {
     //   getSharedPreferences("config", MODE_PRIVATE).edit().putString(key, value).commit();

    }
/*
    //从历史记录存储中读数据
    String getString(String key) {
       // return getSharedPreferences("config", MODE_PRIVATE).getString(key, null);
    }
*/
    protected void createConnection() throws Exception {
        if (ioSession != null) {
            ioSession.close(true);
        }
        ConnectFuture future = connector
                .connect(new InetSocketAddress(
                        strIP, port));
        future.awaitUninterruptibly();// 等待连接创建成功
        ioSession = future.getSession();// 获取会话
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
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
		if(line2.getItemCount()>0){
			line2.clear();
		}
	}

    String content;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.button_jisuan:

				break;
			case -1902://还原数据
				final EditText et = new EditText(getActivity());
				et.setText("data.txt");
				new AlertDialog.Builder(getActivity()).setTitle("输入数据名字").setView(et).setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String[] data = Util.loadFileLinesInPrjDir(et.getText().toString().trim());
						int count = data.length;
						if(count>FFT.POINT_COUNT) {
							count = FFT.POINT_COUNT;
						}
						initLine();
						for (int i=0;i<count;i++){
							float f = Float.parseFloat(data[i]);
							Data pt = new Data(20*i,f);
							time_datas.add(pt);
						}
						new Thread(){
							@Override
							public void run() {
								Runnable run = new RefreshSeriesTask();
								run.run();
								run.run();
System.out.println("刷新完成");							}
						}.start();
					}
				}).setNegativeButton("cancel",null).show();
				break;
		case R.id.btn_save:
            content = "";
            for (int i = 0; i < line1.getItemCount(); i++) {
                content += line1.getX(i) + "\t" + line1.getY(i)
                        + "\n";
            }
            final EditText editText = new EditText(getActivity());
            editText.setHint("输入文件名");
            new AlertDialog.Builder(getActivity()).setTitle("保存数据").setView(editText).setPositiveButton("保存",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = editText.getText().toString().trim();
                    if(name.equals("")){
                        Util.showToast("请输入文件名");
                        return ;
                    }
                    if (Util.saveFileInPrjDir(name+".data", content))
                        Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
                                .show();
                    else
                        Util.showToast("保存失败");
                }
            }).setNegativeButton("取消",null).show();
            break;
            case R.id.btn_curve_pinyu:
				new AsyncTask<Void,Void,Void>(){

					@Override
					protected Void doInBackground(Void... params) {
						if(line2.getItemCount()==0) {
							int size = line1.getItemCount();
							double[] arr = new double[size];
							for (int i = 0; i < size; i++) {
								arr[i] = line1.getY(0);
//							line1.remove(0);
							}
							FFT fft = new FFT(arr);
							int count = fft.count;
							double[] ys = fft.result;
							for (int i = 0; i < count; i++) {
								double x = 25.0 * i / count;
								line2.add(x, ys[i]);
							}
						}
						setMaxMin(line2.getMinX() - 0.1 * (line2.getMaxX() - line2.getMinX()), line2.getMinY() - 0.1 * (line2.getMaxY() - line2.getMinY()), line2.getMaxX() + 0.1 * (line2.getMaxX() - line2.getMinX()), line2.getMaxY() + 0.1 * (line2.getMaxY() - line2.getMinY()));
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						mDataset.removeSeries(0);
						mDataset.addSeries(line2);
						chart.postInvalidate();
//						chart.setEnabled(false);
//						chart.setClickable(false);
					}
				}.execute((Void) null);
			break;
			case R.id.btn_curve_shiyu:
				mDataset.removeSeries(0);
				mDataset.addSeries(line1);
				setMaxMin(line1.getMinX() - 0.1 * (line1.getMaxX() - line1.getMinX()), line1.getMinY() - 0.1 * (line1.getMaxY() - line1.getMinY()), line1.getMaxX() + 0.1 * (line1.getMaxX() - line1.getMinX()), line1.getMaxY() + 0.1 * (line1.getMaxY() - line1.getMinY()));
				chart.postInvalidate();
				break;
		}
	}

	private void setMaxMin(double xmin, double ymin, double xmax, double ymax) {
		mXYMultipleSeriesRenderer.setXAxisMin(xmin);
		mXYMultipleSeriesRenderer.setXAxisMax(xmax);
		mXYMultipleSeriesRenderer.setYAxisMin(ymin);
		mXYMultipleSeriesRenderer.setYAxisMax(ymax);
	}
}
