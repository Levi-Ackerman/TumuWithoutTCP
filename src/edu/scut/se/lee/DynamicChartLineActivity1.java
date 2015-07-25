/*
 * 动态方式和静态折线图方式完全类似，只是在最初并没有为 XYSeries 对象add值，
 * 而是后面动态添加值，并且添加新值后，刷新即可。
 * 此处使用 Timer 和TimerTask进行定时添加新数据
 * 
 * 两种折线图实现几乎完全一样，只是添加点时的处理不同，即方法 initLine(XYSeries series) 中实现不同 
 * 
 */

package edu.scut.se.lee;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicChartLineActivity1 extends Activity {

	private RelativeLayout dynamic_chart_line_layout;
	private String title = "动态折线图方式1";

	// 用于存放每条折线的点数据
	private XYSeries line1, line2;
	// 用于存放所有需要绘制的XYSeries
	private XYMultipleSeriesDataset mDataset;
	// 用于存放每条折线的风格
	private XYSeriesRenderer renderer1, renderer2;
	// 用于存放所有需要绘制的折线的风格
	private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
	private GraphicalView chart;

	// 以下属性用于initLine(XYSeries series)方法中更新数据
	private double[] x, y;
	private int count;
	private int xTemp, yTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dynamic_chart_line_layout = new RelativeLayout(this);
		dynamic_chart_line_layout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(dynamic_chart_line_layout);

//		dynamic_chart_line_layout = (RelativeLayout) findViewById(R.id.dynamic_chart_line_layout);

		initChart();

		x = new double[10];
		y = new double[10];

		refreshChart();

	}

	private void initChart() {
		// 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
		// XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
		line1 = new XYSeries("折线1");
		line2 = new XYSeries("折线2");
		renderer1 = new XYSeriesRenderer();
		renderer2 = new XYSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();
		mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();

		// 对XYSeries和XYSeriesRenderer的对象的参数赋值
		// initLine(line1);
		// initLine(line2);
		initRenderer(renderer1, Color.GREEN, PointStyle.CIRCLE, true);
		initRenderer(renderer2, Color.CYAN, PointStyle.TRIANGLE, true);

		// 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
		mDataset.addSeries(line1);
		mDataset.addSeries(line2);
		mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
		mXYMultipleSeriesRenderer.addSeriesRenderer(renderer2);

		// 配置chart参数
		setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 0, 10, 0, 100,
				Color.RED, Color.WHITE);

		// 通过该函数获取到一个View 对象
		chart = ChartFactory.getLineChartView(this, mDataset,
				mXYMultipleSeriesRenderer);

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
		renderer.setLineWidth(1);
		return renderer;
	}

	protected void setChartSettings(
			XYMultipleSeriesRenderer mXYMultipleSeriesRenderer, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax,
			int axesColor, int labelsColor) {
		// 有关对图表的渲染可参看api文档
		mXYMultipleSeriesRenderer.setChartTitle(title);
		mXYMultipleSeriesRenderer.setXTitle(xTitle);
		mXYMultipleSeriesRenderer.setYTitle(yTitle);
		mXYMultipleSeriesRenderer.setXAxisMin(xMin);
		mXYMultipleSeriesRenderer.setAxisTitleTextSize(30);
		mXYMultipleSeriesRenderer.setChartTitleTextSize(50);
		mXYMultipleSeriesRenderer.setLabelsTextSize(15);
		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
		mXYMultipleSeriesRenderer.setYAxisMin(yMin);
		mXYMultipleSeriesRenderer.setYAxisMax(yMax);
		mXYMultipleSeriesRenderer.setAxesColor(axesColor);
		mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
		mXYMultipleSeriesRenderer.setShowGrid(true);
		mXYMultipleSeriesRenderer.setGridColor(Color.GRAY);
		mXYMultipleSeriesRenderer.setXLabels(20);
		mXYMultipleSeriesRenderer.setYLabels(10);
		mXYMultipleSeriesRenderer.setXTitle("time");
		mXYMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
		mXYMultipleSeriesRenderer.setPointSize((float) 5);
		mXYMultipleSeriesRenderer.setShowLegend(true);
		mXYMultipleSeriesRenderer.setLegendTextSize(20);
	}

	class RefreshSeriesTask extends TimerTask {
		public void run() {
			initLine(line1);
			initLine(line2);
			chart.postInvalidate();
		}
	}

	private void initLine(XYSeries series) {

		Random r = new Random();
		xTemp = 0;
		yTemp = r.nextInt(100);

		count = series.getItemCount();
		if (count > 10) {
			count = 10;
		}

		for (int i = 0; i < count; i++) {
			x[i] = series.getX(i);
			y[i] = series.getY(i);
		}
		series.clear();

		series.add(xTemp, yTemp);

		for (int i = 0; i < count; i++) {
			series.add(x[i] + 1, y[i]);
		}
	}

	private void refreshChart() {
		Timer timer = new Timer();
		timer.schedule(new RefreshSeriesTask(), 0, 1 * 1000);
	}
}
