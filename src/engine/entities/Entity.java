package engine.entities;

import org.lwjgl.opengl.GL11;

import engine.Game;
import engine.Vector;
import engine.gfx.Camera;
import engine.gfx.VectorGraphics;

public abstract class Entity {

		protected Game game;
		protected boolean active;
		
		// coords, speed vector
		protected double x, y;
		protected double dx, dy;
		
		// radius, direction (theta)
		protected double r, t;
		
		public Entity(Game game){
			active=true;
			this.game=game;
			this.x=0;
			this.y=0;
			this.dx=0;
			this.dy=0;
			this.r=0;
			this.t=0;
		}
		
		// obj methods
		public double distanceTo(Entity e){
			return Vector.distanceFromTo(x, y, e.$x(), e.$y());
		}
		public double directionTo(Entity e){
			return Vector.directionFromTo(x, y, e.$x(), e.$y());
		}
		
		public void rotateTowards(Entity e, double av, double dt){
			rotateTowards(this.directionTo(e), av, dt);
		}
		public void rotateTowards(double theta, double av, double dt){
			double tt=Vector.tdistanceFromTo(t, theta);
			double dtt=av*dt;
			if(Math.abs(tt)<dtt){
				t=theta;
			}else if(tt<0){
				t-=dtt;
			}else{
				t+=dtt;
			}
		}
		public void applyAccelerationVector(double t, double amt, double dt){
			dx+=Vector.vectorToDx(t, amt)*dt;
			dy+=Vector.vectorToDy(t, amt)*dt;
		}
		public void setSpeedVector(double t, double amt){
			dx=Vector.vectorToDx(t, amt);
			dy=Vector.vectorToDy(t, amt);
		}
		public void addSpeedVector(double t, double amt){
			dx+=Vector.vectorToDx(t, amt);
			dy+=Vector.vectorToDy(t, amt);
		}
		public void updatePosition(double dt){
			x+=dx*dt;
			y+=dy*dt;
		}
		public boolean intersects(Entity e){
			return Vector.distanceFromTo(this.x, this.y, e.$x(), e.$y())<this.r+e.$r();
		}
		
		public void renderCollisionBounds(Camera c, int seg){
			VectorGraphics.beginVectorRender();
			GL11.glColor4d(1,0,0,1);
			VectorGraphics.renderVectorCircle(this.x, this.y, this.r, seg, c);
			VectorGraphics.endVectorRender();
		}
		public void renderMovementVector(Camera c) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1,0,0,1);
			GL11.glVertex2d(c.$cx($x()), c.$cy($y()));
			GL11.glVertex2d(c.$cx($x()+dx), c.$cy($y()+dy));
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		public abstract void update(double dt);
		public abstract void render(Camera c);
		public abstract void onDeath();
		
		public void deactivate(){
			active=false;
		}
		public boolean isCollidable(){
			return true;
		}
		
		
		// getters/setters
		public double $x(){
			return x;
		}
		public double $y(){
			return y;
		}
		public double $r(){
			return r;
		}
		public double $t(){
			return t;
		}
		public double $speedt(){
			return Vector.dxyToDirection(dx,dy);
		}
		public double $speed(){
			return Vector.dxyToDistance(dx,dy);
		}
		public boolean isActive(){
			return active;
		}
		
		public void offsetXY(double dx, double dy){
			x+=dx;
			y+=dy;
		}
		public void offsetTR(double t, double r){
			x+=Vector.vectorToDx(t, r);
			y+=Vector.vectorToDy(t, r);
		}
		
		public void setXY(double x, double y){
			this.x=x;
			this.y=y;
		}
		public void setBoundRadius(double r){
			this.r=r;
		}
		public void setDirection(double theta){
			this.t=theta;
		}
}
