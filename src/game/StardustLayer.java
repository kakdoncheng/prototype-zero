package game;

import java.util.ArrayList;

import engine.Game;
import engine.Vector;
import engine.gfx.Camera;

public class StardustLayer {
	
	private int bounds;
	private double zoom;
	
	private Game game;
	
	private Camera mainCamera;
	private Camera bgCamera;
	
	private ArrayList<Stardust> stars;
	
	public StardustLayer(Game game, int density, int bounds, double zoom, Camera c){
		this.game=game;
		this.bounds=bounds;
		this.zoom=zoom;
		this.mainCamera=c;
		this.bgCamera=new Camera();
		this.stars=new ArrayList<Stardust>();
		
		for(int i=0;i<density;i++){
			stars.add(new Stardust(game, game.$prng().$double(-bounds, bounds), game.$prng().$double(-bounds, bounds), game.$prng().$int(1, 4)));
		}
		
		bgCamera.hardSetZoom(mainCamera.$zoom()*zoom);
		bgCamera.hardCenterOnPoint(mainCamera.$dx(), mainCamera.$dy());
	}
	
	public void update(double dt){
		bgCamera.hardSetZoom(mainCamera.$zoom()*zoom);
		bgCamera.hardCenterOnPoint(mainCamera.$dx(), mainCamera.$dy());
		for(Stardust a:stars){
			a.update(dt);
			if(Vector.distanceFromTo(a.$x(), a.$y(), bgCamera.$dx(), bgCamera.$dy())>bounds){
				a.offsetTR(Vector.directionFromTo(a.$x(), a.$y(), bgCamera.$dx(), bgCamera.$dy()), bounds*2);
			}
		}
	}
	
	public void render(){
		// only render stars that are in frame
		for(Stardust a:stars){
			double radius=Vector.dxyToDistance(game.$displayWidth()/2, game.$displayHeight()/2)/bgCamera.$zoom();
			if(Vector.distanceFromTo(a.$x(), a.$y(), bgCamera.$dx(), bgCamera.$dy())<radius){
				a.render(bgCamera);
			}
		}
		/*
		VectorGraphics.beginVectorRender();
		GL11.glColor4d(1, 0, 0, 1);
		VectorGraphics.renderVectorCircle(bgCamera.$dx(), bgCamera.$dy(), bounds, 64, bgCamera);
		VectorGraphics.endVectorRender();
		//*/
	}

}
