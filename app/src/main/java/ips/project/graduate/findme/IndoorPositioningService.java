package ips.project.graduate.findme;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

//	class IndoorPositioningService
//	extends Service
//
//	The Android application service for get indoor position of user
//	by scanning BLE devices around.
//
public class IndoorPositioningService extends Service {
	public static final String INTENT_ACTION = "intent.action.ips.service";
	public static final int BROADCAST = 1;
    private boolean flag = false;

	private final RemoteCallbackList<IRemoteServiceCallback> callbacks = new RemoteCallbackList<>();
	private final IRemoteService.Stub binder = new IRemoteService.Stub() {
		@Override
		public boolean registerCallback(IRemoteServiceCallback callback) throws RemoteException {
			return callback != null && callbacks.register(callback);
		}

		@Override
		public boolean unregisterCallback(IRemoteServiceCallback callback) throws RemoteException {
			return callback != null && callbacks.unregister(callback);
		}
	};

	private LeDeviceAdapter LeDeviceAdapter;
	private BluetoothAdapter bluetoothAdapter;
	private MapElements mapElements;

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
                //handle the message for case of 'BROADCAST' event.
				case BROADCAST :
					int nReceiver = callbacks.beginBroadcast();

					for (int i = 0; i < nReceiver; i++) {
						try {
							callbacks.getBroadcastItem(i).valueChanged(mapElements);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					callbacks.finishBroadcast();
					break;
			}

			return false;
		}
	});

	@Override
	public IBinder onBind(Intent intent) { return binder; }

	@Override
	public void onCreate() {
		super.onCreate();
        flag = true;
        new BackgroundService().start();
    }

	@Override
	public void onDestroy() {
		handler.removeMessages(BROADCAST);
		super.onDestroy();
        flag = false;
	}


    //	class BackgroundService
    //	extends Thread
    //
    //	Provide service loop for calculating indoor position of user, in background.
    //	The loop will be continued until the class 'IndoorPositioningService' be destroyed.
    //
	private class BackgroundService extends Thread {
		private static final long SCAN_PERIOD = 5000;

		public BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                LeDeviceAdapter.addTable(device, rssi); // collect rssi data
            }
		};

		@Override
		public void run() {
			final BluetoothManager bluetoothManager =
					(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			bluetoothAdapter = bluetoothManager.getAdapter();

			LeDeviceAdapter = new LeDeviceAdapter();

            //service loop while the flag '(boolean) loop' has value of 'true'.
			while(flag) {
				bluetoothAdapter.startLeScan(leScanCallback);

                //scanning BLE devices for about 5 sec.
				try {
					Thread.sleep(SCAN_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				bluetoothAdapter.stopLeScan(leScanCallback);
                LeDeviceAdapter.addDevice();

                //calculate indoor position of user by using the information of BLE devices around,
                //which are stored in class 'LeDeviceListAdapter'.
                mapElements = new MapElements();
                mapElements.calculate(LeDeviceAdapter.getDevices());

                //notify to handler that calculating is over.
                handler.sendEmptyMessage(BROADCAST);

				LeDeviceAdapter.clear();
			}
		}
	}

}