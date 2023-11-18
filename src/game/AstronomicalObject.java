package game;

import engine.Vector;
import engine.gfx.Camera;
import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class AstronomicalObject {
	
	// physical properties
	protected double x;
	protected double y;
	protected double radius;
	protected double scale;
	
	public double $x(){
		return x;
	}
	public double $y(){
		return y;
	}
	public double $radius(){
		return radius;
	}
	public String $label(){
		return label;
	}
	
	// metadata
	protected String label;
	protected Texture texture;
	
	public AstronomicalObject(double x, double y, double radius, double scale,
			String textureID, String label){
		this.x=x;
		this.y=y;
		this.radius=radius;
		this.scale=scale;
		this.label=label;
		this.texture=TextureLoader.$texture(textureID);
	}
	
	public void render(Camera c){
		// these objects are so large that they should fade out
		// once they approach the render border
		// STARDUST_BOUNDS * STARDUST_ZOOM to match planet fade with starfield
		///*
		double bounds=4096*0.825;//(4096*0.825)+radius;
		double distance=Vector.distanceFromTo(x, y, c.$dx(), c.$dy());
		double alpha=1;
		if(distance-bounds>0){
			double factor=(distance-bounds)/(radius+bounds*0.5);
			alpha=1-factor;
		}
		texture.render(c.$cx(x), c.$cy(y), 0, c.$zoom()*scale, 1, 1, 1, alpha);
		//*/
		//texture.render(c.$cx(x), c.$cy(y), 0, c.$zoom()*scale);	
	}
}
