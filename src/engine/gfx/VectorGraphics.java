package engine.gfx;

import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.input.MouseHandler;

public class VectorGraphics {
	
	public static void renderDotCursor(){
		int seg=16, rs=16;
		double ci=2*Math.PI;
		double cis=ci/seg;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,0.5);
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,rs), 
				dxyy1=Vector.vectorToDy(i,rs), 
				dxyx2=Vector.vectorToDx(i+(ci/seg),rs),
				dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
			GL11.glVertex2d(MouseHandler.$ax()+dxyx1, MouseHandler.$ay()+dxyy1);
			GL11.glVertex2d(MouseHandler.$ax()+dxyx2, MouseHandler.$ay()+dxyy2);
		}
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,1);
		GL11.glVertex2d(MouseHandler.$ax()-1, MouseHandler.$ay()-1);
		GL11.glVertex2d(MouseHandler.$ax()+1, MouseHandler.$ay()+1);
		GL11.glVertex2d(MouseHandler.$ax()-1, MouseHandler.$ay()+1);
		GL11.glVertex2d(MouseHandler.$ax()+1, MouseHandler.$ay()-1);
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	public static void renderCrosshairCursor(){
		int a=6, b=12;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,1);
		GL11.glVertex2d(MouseHandler.$ax()-a, MouseHandler.$ay()-a);
		GL11.glVertex2d(MouseHandler.$ax()-b, MouseHandler.$ay()-b);
		
		GL11.glVertex2d(MouseHandler.$ax()+a, MouseHandler.$ay()+a);
		GL11.glVertex2d(MouseHandler.$ax()+b, MouseHandler.$ay()+b);
		
		GL11.glVertex2d(MouseHandler.$ax()-a, MouseHandler.$ay()+a);
		GL11.glVertex2d(MouseHandler.$ax()-b, MouseHandler.$ay()+b);
		
		GL11.glVertex2d(MouseHandler.$ax()+a, MouseHandler.$ay()-a);
		GL11.glVertex2d(MouseHandler.$ax()+b, MouseHandler.$ay()-b);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public static void beginVectorRender(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
	}
	public static void endVectorRender(){
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	public static void renderVectorCircle(double x, double y, double r, int seg, Camera c){
		double ci=2*Math.PI;
		double cis=ci/seg;
		double lim=seg>=64?ci-cis:ci;
		for(double i=0;i<lim;i+=cis){
			double dxyx1=Vector.vectorToDx(i,r), 
					dxyy1=Vector.vectorToDy(i,r), 
					dxyx2=Vector.vectorToDx(i+(ci/seg),r),
					dxyy2=Vector.vectorToDy(i+(ci/seg),r);
			GL11.glVertex2d(c.$cx(x+dxyx1), c.$cy(y+dxyy1));
			GL11.glVertex2d(c.$cx(x+dxyx2), c.$cy(y+dxyy2));
		}
	}
	public static void renderVectorLines4xy(double[] l, double scale, Camera c){
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
	}
	public static void renderVectorLines4xyM(double[] l, double scale, Camera c){
		for(int i=0; i<l.length; i+=4){
			GL11.glVertex2d(l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
			GL11.glVertex2d(-l[i]*c.$zoom()*scale, l[i+1]*c.$zoom()*scale);
			GL11.glVertex2d(-l[i+2]*c.$zoom()*scale, l[i+3]*c.$zoom()*scale);
		}
	}
}
