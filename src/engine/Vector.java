package engine;

public class Vector {
	
	public static double constrainTheta(double t){
		while(t<0){
			t+=2*Math.PI;
		}
		while(t>=2*Math.PI){
			t-=2*Math.PI;
		}
		return t;
	}
	
	public static double distanceFromTo(double x1, double y1, double x2, double y2){
		double dx=x1-x2, dy=y1-y2;
		return Math.sqrt((dx*dx)+(dy*dy));
	}
	public static double tdistanceFromTo(double t1, double t2){
		t1=Vector.constrainTheta(t1);
		t2=Vector.constrainTheta(t2);
		t2-=t1;
		t1=0;
		if(t2>Math.PI){
			t2-=2*Math.PI;
		}else if(t2<-Math.PI){
			t2+=2*Math.PI;
		}
		return t2-t1;
	}
	public static double directionFromTo(double x1, double y1, double x2, double y2){
		if((int)x1==(int)x2&&(int)y1==(int)y2){
			return 0;
		}
		double rad=Math.atan((y1-y2)/(x1-x2))+Math.PI/2;
		return (x1-x2)<0?rad+Math.PI:rad;
	}
	
	public static double vectorToDx(double t, double dist){
		return -dist*Math.sin(t);
	}
	public static double vectorToDy(double t, double dist){
		return dist*Math.cos(t);
	}
	public static double dxyToDistance(double dx, double dy){
		return distanceFromTo(0,0,dx,dy);
	}
	public static double dxyToDirection(double dx, double dy){
		return directionFromTo(0,0,dx,dy);
	}
}
