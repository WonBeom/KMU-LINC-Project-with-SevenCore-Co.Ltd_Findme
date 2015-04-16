package ips.project.graduate.findme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


//the activity which compose main one,
//has option menu to use debugging mode.
public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {
	private GoogleMap map;
    private boolean lineChk; //flag for checking whether draw the 'lines' or not
    private boolean pointChk; //flag for checking whether draw the 'points' or not

    //callback function which will be called when the value of map elements get changed.
	IRemoteServiceCallback callback = new IRemoteServiceCallback.Stub() {
		@Override
		public void valueChanged(MapElements elem) {
                changeMap(elem);
		}
	};

	IRemoteService remoteService;
	ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if(service == null) return;

			remoteService = IRemoteService.Stub.asInterface(service);

			try {
				remoteService.registerCallback(callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			if(remoteService == null) return;

			try {
				remoteService.unregisterCallback(callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();

        mapFragment.getMapAsync(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case R.id.item01: //line checker
				if(item.isChecked()) {
                    item.setChecked(false);
                    lineChk = false;
                }
				else {
                    item.setChecked(true);
                    lineChk = true;
                }
				break;

			case R.id.item02: //point checker
				if(item.isChecked()) {
                    item.setChecked(false);
                    pointChk = false;
                }
				else {
                    item.setChecked(true);
                    pointChk = true;
                }
				break;
		}

		return true;
	}

	@Override
	protected void onResume() {
		startServiceBind();
		super.onResume();
	}

	@Override
	protected void onStop() {
		stopServiceBind();
		super.onStop();
	}

	@Override
	public void onMapReady(GoogleMap map2) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.510226, 127.038467), 21));
    }

    /* start the service and bind it. */
	private void startServiceBind() {
		startService(new Intent(this, IndoorPositioningService.class));
		bindService(new Intent(IndoorPositioningService.INTENT_ACTION),
				connection, Context.BIND_AUTO_CREATE);
	}

    /* stop the service and unbind it. */
	private void stopServiceBind() {
        unbindService(connection);
        stopService(new Intent(this, IndoorPositioningService.class));
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* the function which refresh google map with new elements */
    public void changeMap(MapElements elem) {
        map.clear();

        Coord[] leDevices = elem.getLeDevices();
        Line[] 	connections = elem.getConnections();
        Line[] 	orthoLines = elem.getOrthoLines();
        Coord[] estimates = elem.getEstimates();
        Coord 	userPosition = elem.getUserPosition();

        if(leDevices == null) return;

        /* add circles which stand for the location of beacon devices */
        for(int i = 0; i < leDevices.length; i++)
            map.addCircle(new CircleOptions().center(new LatLng(leDevices[i].getX(), leDevices[i].getY()))
                                             .radius(0.3).fillColor(Color.BLUE).strokeColor(Color.BLUE));

        if(lineChk) {

            if(connections == null) return;

            /* draw connections */
            for (int i = 0; i < connections.length; i++)
                map.addPolyline(new PolylineOptions()
                        .add(new LatLng(connections[i].getCoord(0).getX(), connections[i].getCoord(0).getY()))
                        .add(new LatLng(connections[i].getCoord(1).getX(), connections[i].getCoord(1).getY()))
                        .color(Color.BLUE));

            if (orthoLines == null) return;

            /* draw orthoLines */
            for (int i = 0; i < orthoLines.length; i++) {
                map.addPolyline(new PolylineOptions()
                        .add(new LatLng(orthoLines[i].getCoord(0).getX(), orthoLines[i].getCoord(0).getY()))
                        .add(new LatLng(orthoLines[i].getCoord(1).getX(), orthoLines[i].getCoord(1).getY()))
                        .color(Color.GREEN));

            }
        }

        if(pointChk) {
            if (estimates == null) return;

            int neverpoint = elem.getNeverpoint();
            /* draw estimates */
            for (int i = 0; i < estimates.length - neverpoint; i++){
                map.addCircle(new CircleOptions().center(new LatLng(estimates[i].getX(), estimates[i].getY()))
                        .radius(0.2).fillColor(Color.GREEN).strokeColor(Color.GREEN));
            }

        }

        if(userPosition == null) return;

        /* pin UserPosition */
        map.addMarker(new MarkerOptions().position(new LatLng(userPosition.getX(), userPosition.getY()))
                                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
    }
}
