package game;

import org.lwjgl.opengl.GL11;

import engine.gfx.Camera;

public class Spark extends PrototypeEntity{
	
	private double timer;
	private double timeout;
	private double brightness;
	private double hue;
	private double alphaN;
	private double minA;
	
	// initial color
	private boolean useInitialColor;
	private double red;
	private double green;
	private double blue;
	
	public void setInitialColor(double r, double g, double b){
		useInitialColor=true;
		red=r;
		green=g;
		blue=b;
	}
	public void setTimeout(double timeout){
		this.timeout=timeout;
	}
	
	public Spark(PrototypeGame game, double x, double y){
		this(game, x, y, game.$prng().$int(-6, 3), game.$prng().$double(0, Math.PI*2), 
				game.$prng().$double(0.25, 8), game.$prng().$double(3, 90));
	}
	
	public Spark(PrototypeGame game, double x, double y, int size, double direction, double speed, double timer) {
		super(game);
		this.setXY(x, y);
		this.setDirection(direction);
		this.setSpeedVector(direction, speed);
		this.setBoundRadius(size);
		if(size<1){
			this.setBoundRadius(1);
		}
		this.timer=0;
		this.timeout=timer;
		this.brightness=game.$prng().$double(0.25, 1);
		this.hue=game.$prng().$double(0.5, 1);
		this.alphaN=game.$prng().$double(-0.25, 1);
		this.minA=r*game.$maxZoom()*0.5;
		
		this.useInitialColor=false;
		this.red=0;
		this.green=0;
		this.blue=0;
	}
	
	public void update(double dt) {
		if(timer>timeout){
			alphaN-=dt*0.5;
			if(alphaN+minA<=0){
				this.deactivate();
			}
		}
		timer+=dt;
		updatePosition(dt);
	}
	
	public void render(Camera c) {
		double rr=r*c.$zoom();
		double rrz=alphaN+(rr*0.5);
		if(rr<1){
			rr=1;
		}
		
		double r=brightness;
		double g=brightness*hue;
		double b=0;
		if(useInitialColor){
			double ht=timer*0.5;
			double m=ht;
			double n=1-ht;
			if(n<=0){
				useInitialColor=false;
			}else{
				r=(r*m)+(red*n);
				g=(g*m)+(green*n);
				b=blue*n;
			}
		}
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		if(rr>1){
			// square
			GL11.glPushMatrix();
			GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(r, g, b, rrz);
			GL11.glVertex2d(-rr/2, -rr/2);
			GL11.glVertex2d(-rr/2, rr/2);
			GL11.glVertex2d(rr/2, rr/2);
			GL11.glVertex2d(rr/2, -rr/2);
			GL11.glEnd();
			GL11.glPopMatrix();
			// 45 deg tilted square
			GL11.glPushMatrix();
			GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
			GL11.glRotated(Math.toDegrees(Math.PI/4), 0, 0, 1);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(r, g, b, rrz);
			GL11.glVertex2d(-rr/2, -rr/2);
			GL11.glVertex2d(-rr/2, rr/2);
			GL11.glVertex2d(rr/2, rr/2);
			GL11.glVertex2d(rr/2, -rr/2);
			GL11.glEnd();
			GL11.glPopMatrix();
		}else{
			// square
			GL11.glPushMatrix();
			GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4d(r, g, b, rrz);
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(0, rr);
			GL11.glVertex2d(rr, rr);
			GL11.glVertex2d(rr, 0);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
