package edu.scut.se.lee.fragment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import net.tsz.afinal.annotation.view.ViewInject;
import edu.scut.se.lee.R;

public class ParamsDisplayFragment extends BaseFragment implements
		SensorEventListener {

	SensorManager sensorManager;
	@ViewInject(id = R.id.tv_display_incline)
	private TextView tv_incline;
	@ViewInject(id = R.id.tv_display_ipaddr)
	private TextView tv_ipaddr;
	private Sensor gyroSensor;

	@Override
	public int getRootRes() {
		// TODO Auto-generated method stub
		return R.layout.fragment_params_display;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		sensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		String ip = null;
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			// wifiManager.setWifiEnabled(true);
			ip = getLocalIpAddress();
		} else {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			ip = intToIp(ipAddress);
		}
		tv_ipaddr.setText(ip);
		tv_incline.setText("暂时无法获取");
		// List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		// for (Sensor s : sensors) {
		// String tempString = "\n" + "  设备名称：" + s.getName() + "\n"
		// + "  设备版本：" + s.getVersion() + "\n" + "  供应商："
		// + s.getVendor() + "\n";
		// TextView tx1 = tv_incline;
		// switch (s.getType()) {
		// case Sensor.TYPE_ACCELEROMETER:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 加速度传感器accelerometer" + tempString);
		// break;
		// case Sensor.TYPE_GYROSCOPE:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 陀螺仪传感器gyroscope" + tempString);
		// break;
		// case Sensor.TYPE_LIGHT:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 环境光线传感器light" + tempString);
		// break;
		// case Sensor.TYPE_MAGNETIC_FIELD:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 电磁场传感器magnetic field" + tempString);
		// break;
		// case Sensor.TYPE_ORIENTATION:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 方向传感器orientation" + tempString);
		// break;
		// case Sensor.TYPE_PRESSURE:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 压力传感器pressure" + tempString);
		// break;
		// case Sensor.TYPE_PROXIMITY:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 距离传感器proximity" + tempString);
		// break;
		// case Sensor.TYPE_TEMPERATURE:
		// tx1.setText(tx1.getText().toString() + s.getType()
		// + " 温度传感器temperature" + tempString);
		// break;
		// default:
		// tx1.setText(tx1.getText().toString() + s.getType() + " 未知传感器"
		// + tempString);
		// break;
		// }
		// }
	}

	public String getLocalIpAddress() {
		try {
			// new AsyncTask<Void, String, String>() {
			//
			// @Override
			// protected String doInBackground(Void... params) {
			// // TODO Auto-generated method stubSocket
			// Socket socket;
			// try {
			// socket = new Socket("http://www.baidu.com", 80);
			// return socket.getLocalAddress().toString();
			// } catch (Exception e) {
			// return e.toString();
			// }
			// // return null;
			// }
			//
			// protected void onPostExecute(String result) {
			// tv_ipaddr.setText(result)
			// };
			// }.execute((Void) null);
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	// @Override
	// public void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// sensorManager.registerListener(this, gyroSensor,
	// SensorManager.SENSOR_DELAY_UI);
	// }
	//
	// @Override
	// public void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// sensorManager.unregisterListener(this);
	// }
	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		tv_incline.setText(event.values[2] + "");
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
