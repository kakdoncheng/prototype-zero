package engine.input;

import org.lwjgl.input.Mouse;

import engine.Vector;
import engine.gfx.Camera;

public class MouseHandler {
	
	private static Camera c;
	private static double dw, dh;
	
	public static void setDisplayDimensions(int w, int h){
		MouseHandler.dw=w;
		MouseHandler.dh=h;
	}
	public static void focusOnCamera(Camera c){
		MouseHandler.c=c;
	}
	
	// actual screen mouse coords
	// lower res fullscreen fix
	// check to see if mouse is locked in screen,
	// if not then snap to border
	public static double $ax(){
		if(Mouse.getX()>dw){
			Mouse.setCursorPosition((int)dw, Mouse.getY());
		}
		return -(dw/2)+Mouse.getX();
	}
	public static double $ay(){
		if(Mouse.getY()>dh){
			Mouse.setCursorPosition(Mouse.getX(), (int)dh);
		}
		return (dh/2)-Mouse.getY();
	}
	
	// world mouse coords
	public static double $mx(){
		if(c!=null){
			return c.$cmx($ax());
		}
		return $ax();
	}
	public static double $my(){
		if(c!=null){
			return c.$cmy($ay());
		}
		return $ay();
	}
	public static double $distanceFromCameraToMouse(){
		if(c!=null){
			return Vector.distanceFromTo(c.$dx(), c.$dy(), $mx(), $my());
		}
		return -1;
	}
	public static double $directionFromCameraToMouse(){
		if(c!=null){
			return Vector.directionFromTo(c.$dx(), c.$dy(), $mx(), $my());
		}
		return -1;
	}

}
