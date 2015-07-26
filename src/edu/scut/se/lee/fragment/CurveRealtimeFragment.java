package edu.scut.se.lee.fragment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import edut.scut.se.lee.util.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
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

import android.app.Activity;
import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
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
    @ViewInject(id = R.id.btn_curve_jiagong, click = "onClick")
	private Button btn_jiagong;
    @ViewInject(id = R.id.btn_curve_save,click = "onClick")
    private Button btn_save;
    @ViewInject(id = R.id.button, click = "onClick")
    private Button  btnSend;
    @ViewInject(id =R.id.editText)
    private EditText etIP;
    @ViewInject(id =R.id.editText3)
    private EditText etPort;

	// 用于存放每条折线的点数据
	private XYSeries line1;
	// 用于存放所有需要绘制的XYSeries
	private XYMultipleSeriesDataset mDataset;
	// 用于存放每条折线的风格
	private XYSeriesRenderer renderer1;
	// 用于存放所有需要绘制的折线的风格
	private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
	private GraphicalView chart;

	private myListener listener;
	private SensorManager sensors;
	private Sensor sensor;

    boolean isAvailable;
    boolean isEable = false;
    private IoSession ioSession;
    private NioSocketConnector connector;
    private IoHandler iohandler ;
    //EditText etContent, etPort;
    private String strIP;
    private int port;
    boolean isOpening = false;

    //tvToast = (TextView) findViewById(R.id.toast);

    @Override
	public int getRootRes() {
		// TODO Auto-generated method stub
		return R.layout.fragment_curve_realtime;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
        //改动：初始化控件switch
        sw_start = (Switch)rootView.findViewById(R.id.sw_curve_start);
		initAcceler();
		initChart();
        isEable=false;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        if (!isOpening) {
                            try {
                                connector = new NioSocketConnector(); // 创建连接客户端
                                connector.setConnectTimeoutMillis(10000); // 设置连接超时
                                // 添加处理器，主要负责收包
                                iohandler = new IoHandler() {
                                    @Override
                                    public void sessionCreated(IoSession ioSession) throws Exception {

                                    }

                                    @Override
                                    public void sessionOpened(IoSession ioSession) throws Exception {

                                    }

                                    @Override
                                    public void sessionClosed(IoSession ioSession) throws Exception {

                                    }

                                    @Override
                                    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {

                                    }

                                    @Override
                                    public void exceptionCaught(IoSession ioSession, Throwable throwable) throws Exception {

                                    }

                                    @Override
                                    public void messageReceived(IoSession ioSession, Object o) throws Exception {

                                    }

                                    @Override
                                    public void messageSent(IoSession ioSession, Object o) throws Exception {

                                    }
                                };
                                connector.setHandler(iohandler);
                                strIP = etIP.getText().toString().trim();
                                // strContent = etContent.getText().toString().trim();
                                port = Integer.parseInt(etPort.getText().toString().trim());
                                setConfig("ip", strIP);
                                setConfig("port", port + "");
                                createConnection();
                                isEable=true;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnSend.setText("停止发送");
                                    }
                                });
                                //manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
                                //manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                                isOpening = !isOpening;
                            } catch (Exception e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  Toast.makeText(CurveRealtimeFragment.this, "连接出错", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        } else {
                            ioSession.close(true);
                            connector.dispose();
                            isEable=false;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    btnSend.setText("发送");
                                    //Toast.makeText(MainActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
                                }
                            });
                            // manager.unregisterListener(listener);
                            isOpening = !isOpening;
                        }
                    }
                }.start();
            }
        });
		// refreshChart();
	}

	long startMillin;
	// FileWriter fileWriter;
	private String text;
	@ViewInject(id = R.id.tv_acc)
	private TextView textView;
	int index = 0;
	double lastSec = 0;
	double lastX = 0, lastY = 0;
	List<Data> datas;

	class Data {
		public Data(long milliSec, float acce) {
			this.milliSec = milliSec;
			this.acce = acce;
		}

		public long milliSec;
		public float acce;
	}

	boolean isFirst = true;

	class myListener implements SensorEventListener {

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
			datas.add(new Data(delay, event.values[2]));

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
		listener = new myListener();
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
		initLine();
		// line2 = new XYSeries("折线2");
		renderer1 = new XYSeriesRenderer();
		// renderer2 = new XYSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();
		mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

		// 对XYSeries和XYSeriesRenderer的对象的参数赋值
		// initLine(line1);
		// initLine(line2);
		initRenderer(renderer1, Color.RED, PointStyle.CIRCLE, true);
		// initRenderer(renderer2, Color.CYAN, PointStyle.TRIANGLE, true);

		// 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
		mDataset.addSeries(line1);
		// mDataset.addSeries(line2);
		mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
		// mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);

		// 配置chart参数
		setChartSettings(mXYMultipleSeriesRenderer, "时间(s)", "加速度(m/s^2)",
				-2.5, 2.5, Color.RED, Color.WHITE);

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

	}

	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
			PointStyle style, boolean fill) {
		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		renderer.setColor(color);
		renderer.setPointStyle(style);
		renderer.setFillPoints(fill);
		renderer.setLineWidth(2);
		return renderer;
	}

	double xMin = 0, xMax = 5;

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
		mXYMultipleSeriesRenderer.setXAxisMin(xMin);
		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
		mXYMultipleSeriesRenderer.setYAxisMin(yMin);
		mXYMultipleSeriesRenderer.setYAxisMax(yMax);
		mXYMultipleSeriesRenderer.setAxesColor(axesColor);
		mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
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

	class RefreshSeriesTask extends TimerTask {
		public void run() {
			// initLine(line1);
			// initLine(line2);
			// System.out.println("refreshing");
			// chart.scrollBy(10, 0);
			int count = datas.size();
              String value;
             byte[] data;
			if (count == 0)
				return;
			if (index == 0) {
				index++;
				lastX = datas.get(0).milliSec;
				lastY = datas.get(0).acce;
				line1.add(lastX * 0.001, lastY);

              if(isEable) {
                  try {
                      value = datas.get(0).milliSec + ";" + datas.get(0).acce + ";";
                      data = (value).getBytes("gbk");//将字符组串转换成GBK字符集，再转换成数组传输
                      ioSession.write(IoBuffer.wrap(data));

                  } catch (UnsupportedEncodingException e) {
                      e.printStackTrace();
                  }
              }
				datas.remove(0);
			} else {
				for (int i = 0; i < count; i++) {
					long sec = datas.get(0).milliSec;
					if (sec < 20 * index) {
						lastX = sec;
						lastY = datas.get(0).acce;
					} else if (sec == 20 * index) {
						lastX = sec;
						lastY = datas.get(0).acce;
                        line1.add(lastX * 0.001, lastY);
                       if (isEable) {
                           try {
                               value = datas.get(0).milliSec + ";" + datas.get(0).acce + ";";
                               data = (value).getBytes("gbk");//将字符组串转换成GBK字符集，再转换成数组传输
                               ioSession.write(IoBuffer.wrap(data));
                           } catch (UnsupportedEncodingException e) {
                               e.printStackTrace();
                           }
                       }
                        index++;
					} else {
						float y = datas.get(0).acce;
						lastY = (y - lastY) / (sec - lastX)
								* (20 * index - lastX) + lastY;
						lastX = 20 * index;
						line1.add(lastX * 0.001, lastY);
                        if (isEable) {
                            try {
                                value = datas.get(0).milliSec + ";" + datas.get(0).acce + ";";
                                data = (value).getBytes("gbk");//将字符组串转换成GBK字符集，再转换成数组传输
                                ioSession.write(IoBuffer.wrap(data));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
						index++;
					}

					datas.remove(0);
				}

			}

			double maxtemp = line1.getMaxX();
			double mintemp = maxtemp - xMax + xMin;
			if (maxtemp >= xMax || maxtemp <= mintemp) {
				// 不在视野里，就要移动窗口
				mXYMultipleSeriesRenderer.setXAxisMax(maxtemp);
				mXYMultipleSeriesRenderer.setXAxisMin(mintemp);
				xMax = maxtemp;
				xMin = mintemp;
			}
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
				datas = new ArrayList<Data>();
				index = 0;
				lastX = lastY = 0;
				isFirst = true;
				// datas.add(new Data(0, 0));
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
		xMin = 0;
		xMax = 1.5;
	}

    String content;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_curve_save:
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
                    if (Util.saveFileInPrjDir(name, content))
                        Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT)
                                .show();
                    else
                        Util.showToast("保存失败");
                }
            }).setNegativeButton("取消",null).show();
            break;
            case R.id.btn_curve_jiagong:
			for (int i = 0; i < line1.getItemCount(); i++) {
				double x = line1.getX(i);
				double y = -line1.getY(i);
				line1.remove(i);
				line1.add(i, x, y);
			}
			chart.postInvalidate();
			Toast.makeText(getActivity(), "反转Y值", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
