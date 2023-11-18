package engine.entities;

import engine.Vector;

public class Hardpoint {
	
	private double rOffset;
	private double tOffset;
	private double direction;
	
	public Hardpoint(double xOffset, double yOffset, double direction){
		this.rOffset=Vector.dxyToDistance(xOffset, yOffset);
		this.tOffset=Vector.dxyToDirection(xOffset, yOffset);
		this.direction=direction;
	}
	
	public double $xOffsetFrom(double t){
		return Vector.vectorToDx(t+tOffset, rOffset);
	}
	public double $yOffsetFrom(double t){
		return Vector.vectorToDy(t+tOffset, rOffset);
	}
	public double $tOffset(){
		return direction;
	}

}
