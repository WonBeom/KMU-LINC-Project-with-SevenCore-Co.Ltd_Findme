package ips.project.graduate.findme;

import android.os.Parcel;
import android.os.Parcelable;

//class for get a line from two end-point coordinates
//or from the slope of line and one coordinate above the line.
public class Line implements Parcelable{
	//based on linear function,
	//y = ax + b

	private Coord cd[];		//start, end coordinates
    private double ycoef;   //coefficient of
    private double slope;	//a : slope
	private double yint;	//b : y-intersect

    //get a line from two end-point coordinates
	public Line(Coord c1, Coord c2) {
		cd = new Coord[2];

		cd[0] = c1;
		cd[1] = c2;

        /* when the line is horizontal*/
        if( cd[0].getY() - cd[1].getY() == (double)0 ) {
            ycoef = 1;
            slope = 0;
            yint = c1.getY();
        }
        /* when the line is vertical*/
        else if( cd[0].getX() - cd[1].getX() == (double)0 ) {
            ycoef = 0;
            slope = 1;
            yint = -c1.getX();
        }
        //and else...
		else {
            ycoef = 1;
            slope = (cd[0].getY() - cd[1].getY()) / (cd[0].getX() - cd[1].getX());
            yint = c1.getY() - (slope * c1.getX());
        }
	}

    //get a line from the slope of line and one coordinate above the line
	public Line(double y, double s, Coord c) {
		cd = new Coord[2];

		cd[0] = c;

        ycoef = y; // y의 계수
		slope = s; // 기울기

        // 수직선에 직교하는 선의 방정식을 구함
        if(ycoef==0 && slope==-1) {
            ycoef = 1;
            slope = 0;
            yint = c.getY();
        }
        // 수평선에 직교하는 선의 방정식을 구함
        else if(ycoef==1 && slope==0) {
            ycoef = 0;
            slope = 1;
            yint = -c.getX();
        }
        // 나머지 직선에 직교하는 선의 방정식을 구함.
        else
            yint = c.getY() - (slope * c.getX());

        /* x = b 꼴의 수직선 일 때의 직선 위의 두 점을 구함 */
        if(ycoef==0 && slope==1)
        {
            cd[0] = new Coord(c.getX(), c.getY()+0.0005);
            cd[1] = new Coord(c.getX(), c.getY()-0.0005);
        }
        /* y = b (수평선) 또는 나머지 직선의 직선 위의 두 점을 구함 */
        else {
            cd[0] = new Coord(c.getX() + 0.0005, (slope * (c.getX() + 0.0005) + yint) / ycoef);
            cd[1] = new Coord(c.getX() - 0.0005, (slope * (c.getX() - 0.0005) + yint) / ycoef);
        }
	}

    /* getter 함수 */
    public double getYcoef() { return ycoef;}
	public double getSlope() { return slope; }
	public double getYint() { return yint; }
	public Coord getCoord(int i) { return cd[i]; }

    //get an orthogonal line from the line which is descripted in this class, and return it.
    //(the parameters 'n', 'm' stand for the relative location)
    //(cf. n : m)
	public Line getOrthoLine(double n, double m) {
		double s, x, y;

        /* when the line is horizontal*/
        if(ycoef==1 && slope == 0)
            s = 0;
        //and else...
        else
		    s = -1 / slope;

        /* distance를 이용하여 상대적인 위치를 구함 */
		x = (n * cd[1].getX() + m * cd[0].getX()) / (n + m);
		y = (n * cd[1].getY() + m * cd[0].getY()) / (n + m);

		return new Line(ycoef, s, new Coord(x, y));
	}

	//The methods for 'parcelable'.
	public static final Creator<Line> CREATOR = new Parcelable.Creator<Line>() {
		public Line createFromParcel(Parcel in) {
			return new Line(in);
		}

		public Line[] newArray(int size) {
			return new Line[size];
		}
	};

	private Line(Parcel in) {
		cd = (Coord[]) in.readParcelableArray(Coord.class.getClassLoader());
		slope = in.readDouble();
		yint = in.readDouble();
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(cd, 0);
		dest.writeDouble(slope);
		dest.writeDouble(yint);
	}

}