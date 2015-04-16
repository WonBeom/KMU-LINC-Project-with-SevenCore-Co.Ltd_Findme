package ips.project.graduate.findme;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

//the class which calaulates user position by using the location and distance of beacons.
//(it also calcultes all elements which are needed to get user position)
public class MapElements implements Parcelable {
	private Coord[] leDevices; //the coordinates of beacon devices
	private Line[] 	connections; //the coordinates of the connection between two beacon devices
	private Line[] 	orthoLines; //the orhogonal lines of connections
	private Coord[] estimates; //the coordinates of estimated user position
	private Coord 	userPosition; //the coordinates of user position

    private int neverpoint;

    public Coord[] getLeDevices() {
        return leDevices;
    }
    public Coord[] getEstimates() {
        return estimates;
    }
    public Line[] getConnections() {
        return connections;
    }
    public Line[] getOrthoLines() {
        return orthoLines;
    }
    public Coord getUserPosition() {
        return userPosition;
    }
    public int getNeverpoint() { return neverpoint; }


    public MapElements() { }

    //calculate the elements (means the variables of this class)
	public void calculate(ArrayList<LeDeviceAdapter.LeDevice> devices) {

		int num = devices.size(); //the number of beacon devices
		int count;

		if(num == 0) return;

		leDevices = new Coord[num];

		for(int i = 0; i < num; i++)
			leDevices[i] = new Coord(devices.get(i).getCoord());

		if(num == 1) return;

        //Calculate connections.
        //note that the number of connections is 'num*(num-1)/2'
		connections = new Line[num*(num-1)/2];

		count = 0;
		for(int i = 0; i < num-1; i++) {
			for (int j = i+1; j < num; j++) {
				connections[count] = new Line(leDevices[i], leDevices[j]);
				++count;
			}
		}

        //Calculate orthogonal lines.
		orthoLines = new Line[num*(num-1)/2];

		count = 0;
		for(int i = 0; i < num-1; i++) {
			for (int j = i+1; j < num; j++) {
				orthoLines[count] = connections[count]
						.getOrthoLine(devices.get(i).getDistance(), devices.get(j).getDistance());
				++count;
			}
		}

		num = num*(num-1)/2;
		if(num == 1) return;

        //Calculate estimates.
		estimates = new Coord[num*(num-1)/2];
		double x = 0.0, y = 0.0;
        count = 0;

        for(int i = 0; i < num-1; i++) {
			for (int j = i+1; j < num; j++) {
                Coord estimate = new Coord(orthoLines[i], orthoLines[j]);
                    if(estimate.getX() > (orthoLines[i].getCoord(0).getX() + orthoLines[i].getCoord(1).getX()) / 2 + 0.009
                            || estimate.getX() < (orthoLines[i].getCoord(0).getX() + orthoLines[i].getCoord(1).getX()) / 2 - 0.009
                            || estimate.getY() > (orthoLines[i].getCoord(0).getY() + orthoLines[i].getCoord(1).getY()) / 2 + 0.0012
                            || estimate.getY() < (orthoLines[i].getCoord(0).getY() + orthoLines[i].getCoord(1).getY()) / 2 - 0.0012) {
                        neverpoint++;
                    continue;
                }

                estimates[count] = estimate;
				x += estimates[count].getX();
				y += estimates[count].getY();
				++count;
			}
		}

        num = estimates.length - neverpoint;

        //Calculate user position.
        //assume that the user position would be average location of estimated ones.
		userPosition = new Coord(x/num, y/num);
	}

	//The methods for 'parcelable'.
	public static final Creator<MapElements> CREATOR = new Parcelable.Creator<MapElements>() {
		public MapElements createFromParcel(Parcel in) {
			return new MapElements(in);
		}

		public MapElements[] newArray(int size) {
			return new MapElements[size];
		}
	};

	private MapElements(Parcel in) {
		leDevices = (Coord[]) in.readParcelableArray(Coord.class.getClassLoader());
		connections = (Line[]) in.readParcelableArray(Line.class.getClassLoader());
		orthoLines = (Line[]) in.readParcelableArray(Line.class.getClassLoader());
		estimates = (Coord[]) in.readParcelableArray(Coord.class.getClassLoader());
		userPosition = in.readParcelable(Coord.class.getClassLoader());
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(leDevices, 0);
		dest.writeParcelableArray(connections, 0);
		dest.writeParcelableArray(orthoLines, 0);
		dest.writeParcelableArray(estimates, 0);
		dest.writeParcelable(userPosition, 0);
	}
}
