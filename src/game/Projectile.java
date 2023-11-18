package game;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.entities.Hitbox;
import engine.gfx.Camera;
import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class Projectile extends PrototypeEntity{
	
	private double timer;
	private double lx;
	private double ly;
	
	private double damage;
	private double heatDamage;
	private double energyDamage;
	private double armorPenetration;
	private double hitForce;
	
	public double $lx(){
		return lx;
	}
	public double $ly(){
		return ly;
	}
	
	// backup render color
	private double red;
	private double green;
	private double blue;
	
	// diffuse color
	private boolean defaultColor;
	private double dRed;
	private double dGreen;
	private double dBlue;
	
	private Spacecraft owner;
	private Texture texture;
	
	public Projectile(PrototypeGame game, Spacecraft owner, Texture texture, double x, double y, double direction, 
			double damage, double heatDamage, double energyDamage, double armorPenetration, double hitForce, 
			double speed, double range, double r, double g, double b, double dr, double dg, double db) {
		super(game);
		this.setXY(x, y);
		this.setDirection(direction);
		this.setSpeedVector(direction, speed);
		this.setBoundRadius(1);
		this.owner=owner;
		this.texture=texture;
		this.timer=range/speed;
		
		this.lx=x;
		this.ly=y;
		
		this.damage=damage;
		this.heatDamage=heatDamage;
		this.energyDamage=energyDamage;
		this.armorPenetration=armorPenetration;
		this.hitForce=hitForce;
		
		this.red=r;
		this.green=g;
		this.blue=b;
		
		this.defaultColor=dr<0&&dg<0&&db<0;
		this.dRed=dr;
		this.dGreen=dg;
		this.dBlue=db;
	}
	
	public void update(double dt) {
		if(!active){
			return;
		}
		
		// lifetime
		if(timer<0){
			this.deactivate();
		}
		timer-=dt;
		
		// set last position,
		// then update
		lx=x;
		ly=y;
		updatePosition(dt);
		
		// naive collision detection
		ArrayList<Spacecraft> es=game.$currentState().$ships().$entities();
		for(Spacecraft s:es){
			// ignore owner
			if(s.equals(owner)){
				continue;
			}
			for(Hitbox h:s.$hitboxes()){
				if(h.intersectsWith(lx, ly, x, y)){
					// knockback
					//s.damage(owner, damage);
					s.applyForce(hitForce, t, 1);
					this.deactivate();
					// hit sparks
					// amount based on damage?
					int sm=8;
					int amt=game.$prng().$int(2, 8)+sm;
					double hx=h.$x();
					double hy=h.$y();
					double sr=h.$radius();
					double st=Vector.directionFromTo(hx, hy, x, y);
					double sx=hx+Vector.vectorToDx(st, sr);
					double sy=hy+Vector.vectorToDy(st, sr);
					for(int i=0;i<amt;i++){
						Spark se=new Spark(game, sx, sy);
						se.setBoundRadius(se.$r()*2);
						if(i<sm){
							se.setBoundRadius(1);
							se.setTimeout(2);
						}
						if(defaultColor){
							se.setInitialColor(dRed, dGreen, dBlue);
						}
						se.addSpeedVector(t+game.$prng().$double(-0.02, 0.02), $speed()*game.$prng().$double(0.003125, 0.125));
						game.$currentState().addParticle(se);
					}
					return;
				}
			}
		}
	}
	
	public void render(Camera c) {
		
		// backup render if visible texture too small
		//*
		if(c.$zoom()<0.5){
			double rr=4;
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			//GL11.glBegin(GL11.GL_LINES);
			// adjustable base color?
			GL11.glColor4d(red, green, blue, 1);
			//GL11.glVertex2d(c.$cx(x-0.5), c.$cy(y-0.5));
			//GL11.glVertex2d(c.$cx(x+0.5), c.$cy(y+0.5));
			GL11.glTranslated(c.$cx(x), c.$cy(y), 0);
			GL11.glBegin(GL11.GL_QUADS);
			
			GL11.glVertex2d(-rr/2, -rr/2);
			GL11.glVertex2d(-rr/2, rr/2);
			GL11.glVertex2d(rr/2, rr/2);
			GL11.glVertex2d(rr/2, -rr/2);
			
			//GL11.glVertex2d(-rr, -rr);
			//GL11.glVertex2d(-rr, 0);
			//GL11.glVertex2d(0, 0);
			//GL11.glVertex2d(0, -rr);
			
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		//*/
		
		// render texture
		texture.render(c.$cx(x), c.$cy(y), Math.toDegrees(t)+180, c.$zoom());
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
