package game;

import java.util.ArrayList;
import java.util.HashSet;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.gfx.Camera;
import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class Minimap {
	
	//private PrototypeGame game;
	private Texture bgt;
	
	private double mapx;
	private double mapy;
	private double radius;
	private double scale;
	
	public Minimap(PrototypeGame game, double maxRange){
		//this.game=game;
		this.bgt=TextureLoader.$texture("default-minimap");
		this.radius=128;
		this.mapx=radius+4-(game.$displayWidth()/2);
		this.mapy=radius+4-(game.$displayHeight()/2);
		this.scale=radius/maxRange;
	}
	
	public void render(Spacecraft controllerShip, HashSet<Spacecraft> targetedShips, HashSet<AstronomicalObject> targetedPlanets,
			ArrayList<Spacecraft> ships,
			ArrayList<Projectile> projectiles,
			ArrayList<AstronomicalObject> planets, Camera c){
		
		// render background map texture first
		bgt.render((int)mapx, (int)mapy, 0, 1, 1, 1, 1, 0.25);
		
		// render planets as circles
		///*
		for(AstronomicalObject ao:planets){
			double alpha=0.5;
			if(targetedPlanets.contains(ao)){
				alpha=1;
			}
			double et=Vector.directionFromTo(ao.$x(), ao.$y(), controllerShip.$x(), controllerShip.$y());
			double edx=Vector.distanceFromTo(ao.$x(), ao.$y(), controllerShip.$x(), controllerShip.$y());
			double ex=Vector.vectorToDx(et, edx);
			double ey=Vector.vectorToDy(et, edx);
			if(edx*scale>radius){
				ex=Vector.vectorToDx(et, radius/scale);
				ey=Vector.vectorToDy(et, radius/scale);
			}
			double er=ao.$radius()/32;
			int seg=32, rs=(int)(er);
			double ci=2*Math.PI;
			double cis=ci/seg;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor4d(1, 0, 0, alpha);
			for(double i=0;i<ci;i+=cis){
				double dxyx1=Vector.vectorToDx(i,rs), 
					dxyy1=Vector.vectorToDy(i,rs), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),rs),
					dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
				GL11.glVertex2d(mapx+dxyx1-(ex*scale), mapy+dxyy1-(ey*scale));
				GL11.glVertex2d(mapx+dxyx2-(ex*scale), mapy+dxyy2-(ey*scale));
			}
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		//*/
		
		// render ships
		// two squares to make a dot
		///*
		for(Spacecraft ship:ships){
			if(ship.equals(controllerShip)){
				continue;
			}
			double alpha=0.25;
			if(targetedShips.contains(ship)){
				alpha=1;
			}
			double et=Vector.directionFromTo(ship.$x(), ship.$y(), controllerShip.$x(), controllerShip.$y());
			double edx=Vector.distanceFromTo(ship.$x(), ship.$y(), controllerShip.$x(), controllerShip.$y());
			double ex=Vector.vectorToDx(et, edx);
			double ey=Vector.vectorToDy(et, edx);
			if(edx*scale>radius){
				ex=Vector.vectorToDx(et, radius/scale);
				ey=Vector.vectorToDy(et, radius/scale);
			}
			double r=1.5;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslated(mapx-(ex*scale), mapy-(ey*scale), 0);
			
			GL11.glColor4d(1, 0, 0, alpha);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex2d(-r, -r);
			GL11.glVertex2d(-r, r);
			GL11.glVertex2d(r, r);
			GL11.glVertex2d(r, r);
			GL11.glVertex2d(r, -r);
			GL11.glVertex2d(-r, -r);
			GL11.glEnd();
			
			GL11.glRotated(45, 0, 0, 1);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex2d(-r, -r);
			GL11.glVertex2d(-r, r);
			GL11.glVertex2d(r, r);
			GL11.glVertex2d(r, r);
			GL11.glVertex2d(r, -r);
			GL11.glVertex2d(-r, -r);
			GL11.glEnd();
			
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		//*/
		
		// render center ship
		///*
		double t=controllerShip.$t();
		double rr=4;
		double rrs=rr*0.8;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPushMatrix();
		GL11.glTranslated(mapx, mapy, 0);
		GL11.glRotated(Math.toDegrees(t)+180, 0, 0, 1);
		GL11.glColor4d(1, 1, 1, 0.5);
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glVertex2d(0, -rr);
		GL11.glVertex2d(-rrs, rr);
		GL11.glVertex2d(0, rr);
		GL11.glVertex2d(0, -rr);
		GL11.glVertex2d(rrs, rr);
		GL11.glVertex2d(0, rr);
		GL11.glEnd();
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//*/
	}

}
