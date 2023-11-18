package engine.gfx;

import engine.Vector;
import engine.entities.Entity;

public class Camera {
	
	private boolean smoothZoom;
	private double dx, dy;
	private double zoom, tzoom, rzoom;
	private double minz, maxz;
	
	public Camera(){
		dx=0;
		dy=0;
		smoothZoom=true;
		zoom=1;
		tzoom=1;
		rzoom=0.8;
		minz=0;
		maxz=4;
	}
	
	public void hardCenterOnPoint(double x, double y){
		dx=x;
		dy=y;
	}
	
	public void hardCenterOnEntity(Entity e){
		dx=e.$x();
		dy=e.$y();
	}
	
	public void centerOnEntity(Entity e, double dt){
		if(e==null){
			return;
		}
		if((int)dx!=(int)e.$x()||(int)dy!=(int)e.$y()){
			double dist=Vector.distanceFromTo(dx, dy, e.$x(), e.$y());
			double t=Vector.directionFromTo(dx, dy, e.$x(), e.$y());
			double speed=dist*5;
			if(speed<30){
				speed=30;
			}
			dx+=Vector.vectorToDx(t, speed*dt);
			dy+=Vector.vectorToDy(t, speed*dt);
		}
	}
	
	public void setMinZoom(double min){
		minz=min;
	}
	public void setMaxZoom(double max){
		maxz=max;
	}
	
	public void updateZoom(double dt){
		if(smoothZoom){
			double dz=tzoom-zoom;
			double ddz=rzoom*dt;
			if(Math.abs(dz)<ddz){
				zoom=tzoom;
			}else{
				if(dz<0){
					zoom-=ddz;
				}else{
					zoom+=ddz;
				}
			}
		}else{
			zoom=tzoom;
		}
	}
	public void changeZoom(double amt){
		tzoom+=amt;
		if(tzoom<minz){
			tzoom=minz;
		}
		if(tzoom>maxz){
			tzoom=maxz;
		}
	}
	public void setZoom(double amt){
		tzoom=amt;
		if(tzoom<minz){
			tzoom=minz;
		}
		if(tzoom>maxz){
			tzoom=maxz;
		}
	}
	public void hardSetZoom(double amt){
		setZoom(amt);
		zoom=tzoom;
	}
	
	public void dxy(double x, double y){
		dx+=x;
		dy+=y;
	}
	
	public double $dx(){
		return dx;
	}
	public double $dy(){
		return dy;
	}
	
	public int $cmx(double x){
		return (int)(x/zoom+dx);
	}
	public int $cmy(double y){
		return (int)(y/zoom+dy);
	}
	public int $cx(double x){
		return (int)((x-dx)*zoom);
	}
	public int $cy(double y){
		return (int)((y-dy)*zoom);
	}
	public double $zoom(){
		return zoom;
	}
	
	public String toString(){
		return super.toString()+" dx:"+String.format("%.1f",dx)+" dy:"+String.format("%.1f",dy)+" z:"+String.format("%.1f",zoom);
	}
}
