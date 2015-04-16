package ips.project.graduate.findme;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.util.ArrayMap;
import java.util.ArrayList;

//target to 19 of upper level of api for the functions of class 'ArrayMap'
@TargetApi(19)
//register the distances of beacon devices from rssi value of each,
//with formular : rssi = -(10-n) * log(d) - A
public class LeDeviceAdapter {
    /* rssi의 구하는 공식의 수들. rssi = -(10-n) * log(d) - A */
    private final static int CPWR = -60; //constant 'A' from the formular above
    private final static int NPWR = -10;
    private final static int N = 2; // constant 'n' from the formular above

    private ArrayMap<String,Integer> table;
	private ArrayList<LeDevice> devices;

	public LeDeviceAdapter() {
        table = new ArrayMap<>();
		devices = new ArrayList<>();
	}

    //add a beacon device to '(ArrayMap<String, Integer>) table'
    //only when the device exists in class 'Beacon'.
    public void addTable(BluetoothDevice device, int rssi) {

        if(!Beacon.existKey(device.getAddress())) return;

        if(!table.containsKey(device.getAddress())) {
            table.put(device.getAddress(), rssi);
        }
        else
        {
            Integer sum = (rssi + table.get(device.getAddress())) / 2;
            table.put(device.getAddress(), sum);
        }

    }

    //add device to '(ArrayList<LeDevice>) devices'
    public void addDevice() {
        if(table.isEmpty()) return;

        for(int i=0; i<table.size(); i++) {
            devices.add(new LeDevice(Beacon.lookup(table.keyAt(i)), table.get(table.keyAt(i))));
        }
	}

	public ArrayList<LeDevice> getDevices() { return devices; }

	public void clear() {
        table.clear();
        devices.clear();
    }

    //the class which contains the coordinate
    //and distance (to user device) of one beacon device*/
	public class LeDevice {
		private Coord coord;
		private double distance;

        //formular to get distance from rssi value :
        //d = 10^( (rssi - A) / -(10 * n) )
        //(from the formular above)
		public LeDevice(Coord coord, int rssi) {
			this.coord = coord;
			this.distance = Math.pow(10, (double) (rssi - CPWR)/(NPWR * N));
		}

		public Coord getCoord() { return coord; }
		public double getDistance() { return distance; }
	}
}
