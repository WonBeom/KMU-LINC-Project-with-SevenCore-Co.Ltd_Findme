package ips.project.graduate.findme;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 The class contains the coordinate for one point.
 */
public class Coord implements Parcelable{
	private double x;
	private double y;

	public Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Coord(Coord c) {
		this.x = c.getX();
		this.y = c.getY();
	}

    //creator function for create an object of class 'Coord'
    //from an intersecting point of two lines.
	public Coord(Line l1, Line l2) {
        // 기본 틀은 y = ax + b
        double y1 = l1.getYcoef(), y2 = l2.getYcoef();
        double s1 = l1.getSlope(), s2 = l2.getSlope();
        double a1 = l1.getYint(),  a2 = l2.getYint();

        //the case of no intersecting point exists,
        //which means two lines have same slopes.
        if(s1 == s2 || (y1==0 && y2==0)) {
		    Log.e("class Coord", "Non-exist intersecting point.");
			return;
		}

        //case : when 'l1' is vertical
        if(y1 == 0)
        {
            x = -a1; // y1이 수직선이므로 x는 일정
            y = s2 * x + a2;
        }
        //case : when 'l2' is vertical
        else if(y2 == 0)
        {
            x = -a2; // y2가 수직선이므로 x가 일정
            y = s1 * x + a1;
        }
        //case : when 'l1' is horizontal
        else if(s1 == 0)
        {
            y = a1; // y1이 수평선이므로 y가 일정
            x = (y - a2) / s2;
        }
        //case : when 'l2' is horizontal
        else if(s2 == 0)
        {
            y = a2; // y2가 수평선이므로 y가 일정
            x = (y - a1) / s1;
        }
        //case : usual cases
        else
        {
            x = (a2 - a1) / (s1 - s2);
            y = s1 * x + a1;
        }

	}

	public double getX() { return x; }
	public double getY() { return y; }

	//The methods for 'parcelable'.
	public static final Creator<Coord> CREATOR = new Parcelable.Creator<Coord>() {
		public Coord createFromParcel(Parcel in) {
			return new Coord(in);
		}

		public Coord[] newArray(int size) {
			return new Coord[size];
		}
	};

	private Coord(Parcel in) {
		x = in.readDouble();
		y = in.readDouble();
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
	}

}