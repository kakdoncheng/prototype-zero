package game;

import org.lwjgl.opengl.GL11;

import engine.Game;
import engine.entities.Entity;
import engine.gfx.Camera;

public class Stardust extends Entity{
	
	public Stardust(Game game, double x, double y, int size) {
		super(game);
		this.setXY(x, y);
		this.setDirection(Math.PI/4);
		//this.setSpeedVector(0, 24);
		if(size<0){
			this.setBoundRadius(1);
		}else{
			this.setBoundRadius(size);
		}
	}
	
	public void update(double dt) {
		//updatePosition(dt);
	}
	
	public void render(Camera c) {
		double rr=r*c.$zoom();
		double rrz=rr*0.5;
		if(rr<2){
			rr=2;
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(c.$cx(x), c.$cy(y), 0);
		GL11.glRotatef((float)Math.toDegrees(t), 0, 0, 1);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4d(1, 1, 1, rrz);
		GL11.glVertex2d(-rr/2, -rr/2);
		GL11.glVertex2d(-rr/2, rr/2);
		GL11.glVertex2d(rr/2, rr/2);
		GL11.glVertex2d(rr/2, -rr/2);
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
	}
	
	public boolean isCollidable(){
		return false;
	}

	public void onDeath() {
		
	}
}
