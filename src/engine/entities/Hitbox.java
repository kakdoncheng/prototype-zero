package engine.entities;

import engine.Vector;

public class Hitbox {
	
	private Entity owner;
	private Hardpoint offset;
	private double radius;
	
	public Hitbox(Entity owner, double xOffset, double yOffset, double radius){
		this(owner, new Hardpoint(xOffset, yOffset, 0), radius);
	}
	public Hitbox(Entity owner, Hardpoint offset, double radius){
		this.owner=owner;
		this.offset=offset;
		this.radius=radius;
	}
	
	public void setOwner(Entity e){
		owner=e;
	}
	
	public Entity $owner(){
		return owner;
	}
	public double $x(){
		return owner.$x()+offset.$xOffsetFrom(owner.$t());
	}
	public double $y(){
		return owner.$y()+offset.$yOffsetFrom(owner.$t());
	}
	public double $radius(){
		return radius;
	}
	
	public boolean intersectsWith(Hitbox h){
		return Vector.distanceFromTo(this.$x(), this.$y(), h.$x(), h.$y()) < this.$radius()+h.$radius();
	}
	// https://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
	public boolean intersectsWith(double x1, double y1, double x2, double y2){
		// direction vector of line segment AB
		double dx=x2-x1;
		double dy=y2-y1;
		// direction vector of center point to A
		double fx=x1-this.$x();
		double fy=y1-this.$y();
		// quadratic function
		double r=this.$radius();
		double a=(dx*dx)+(dy*dy); // AB dot AB
		double b=((fx*dx)+(fy*dy))*2; // (centerA dot AB) * 2
		double c=((fx*fx)+(fy*fy))-(r*r); // (centerA dot centerA) - r^2
		double d=(b*b)-(4*a*c); // discriminant
		
		// two solutions, six cases
		//          -o->             --|-->  |            |  --|->
		// Impale(t1 hit,t2 hit), Poke(t1 hit,t2>1), ExitWound(t1<0, t2 hit),
		//       ->  o                     o ->              | -> |
		// FallShort (t1>1,t2>1), Past (t1<0,t2<0), CompletelyInside(t1<0, t2>1)
		if(d>0){
			d=Math.sqrt(d);
			double t1=(-b-d)/(2*a);
			double t2=(-b-d)/(2*a);
			// impale, poke
			if(t1>=0 && t1<=1){
				return true;
			}
			// exit wound
			if(t2>=0 && t2<=1){
				return true;
			}
		}
		return false;
	}
	
	public Hitbox clone(){
		return new Hitbox(owner, offset, radius);
	}

}
