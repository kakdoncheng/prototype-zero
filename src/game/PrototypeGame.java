package game;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import engine.Game;
import engine.State;
import engine.gfx.Camera;
import engine.gfx.StringGraphics;
import engine.gfx.TextureLoader;
import engine.input.MouseHandler;

public class PrototypeGame extends Game{
	
	public static final String credits="Game Design, Programming, & Art: Linh-Han Van 2023.09.29";
	public static final String version="в0.20230923"; //абвг
	
	private static final int STARDUST_LAYERS=3;
	private static final int STARDUST_BOUNDS=4096;
	private static final int STARDUST_DENSITY=128;
	private static final double STARDUST_ZOOM=0.825;
	
	// 1m = 4px
	// 0.25m = 1px
	private static final double METERS_TO_PIXELS_CONVERSION_RATE=4;
	public double convertMetersToPixels(double meters){
		return meters*METERS_TO_PIXELS_CONVERSION_RATE;
	}
	public double convertPixelsToMeters(double pixels){
		return pixels/METERS_TO_PIXELS_CONVERSION_RATE;
	}
	
	private ArrayList<StardustLayer> starfield;
	public void reloadBackgroundStars(){
		starfield=new ArrayList<StardustLayer>();
		for(int i=0; i<STARDUST_LAYERS; i+=1){
			starfield.add(new StardustLayer(this, STARDUST_DENSITY*(int)Math.pow(2, i), STARDUST_BOUNDS*(int)Math.pow(2, i), STARDUST_ZOOM/(int)Math.pow(2, i), camera));
		}
	}
	
	private boolean showDebugText;
	public void showDebugText(boolean yes){
		showDebugText=yes;
	}
	private boolean renderbg;
	public void renderBackgroundStars(boolean yes){
		renderbg=yes;
	}
	
	private ArrayList<String> debugText;
	public void addDebugText(String text){
		if(!showDebugText){
			return;
		}
		if(debugText!=null){
			debugText.add(text);
		}
	}
	
	private Camera camera;
	public Camera $camera(){
		return camera;
	}
	public double $minZoom(){
		return 0.18;
	}
	public double $maxZoom(){
		return 2.0;
	}

	public PrototypeGame() {
		super(0, 0, "Prototype Zero");
		init();
		loop();
	}
	
	public PrototypeState $currentState(){
		return (PrototypeState)State.$currentState();
	}

	public void init() {
		active=true;
		showDebugText=false;
		debugText=new ArrayList<String>();
		
		setFullscreen(true);
		setFixedStep(true);
		setResolution(1280, 720);
		setFPSLimit(60);
		
		// slow mode
		//setFPSLimit(30);
		//setRunSpeed(0.5);
		
		createDisplay();
		
		camera=new Camera();
		camera.setMinZoom($minZoom());
		camera.setMaxZoom($maxZoom());
		
		Mouse.setGrabbed(true);
		Mouse.setCursorPosition($displayWidth()/2, $displayHeight()/2+128);
		MouseHandler.setDisplayDimensions($displayWidth(), $displayHeight());
		MouseHandler.focusOnCamera(camera);
		
		TextureLoader.init();
		StringGraphics.init();
		
		State.addState(0, new LoadingState(this));
		State.addState(1, new PrototypeState(this));
		State.setCurrentState(0);
		State.$currentState().reset();
		
	}

	public void update(double dt) {
		// debug text
		if(showDebugText){
			// fps, cap, and ram
			addDebugText(String.format("FPS %.1f",$FPS()));
			addDebugText(String.format("CAP %.1f%%",$tickCapacity()));
			addDebugText(String.format("RAM %.1f/%.1f MB",(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1048576.0f, Runtime.getRuntime().totalMemory()/1048576.0f));
			addDebugText(String.format("%dx%d",$displayWidth(),$displayHeight()));
			addDebugText("v.0a20231112");
			addDebugText(" ");
			// window vs world coords
			addDebugText("ax "+MouseHandler.$ax()+" ay "+MouseHandler.$ay());
			addDebugText("cx "+MouseHandler.$mx()+" cy "+MouseHandler.$my());
			addDebugText(String.format("dxcm %.1f m", convertPixelsToMeters(MouseHandler.$distanceFromCameraToMouse())));
			addDebugText(String.format("dtcm %.2fπ", MouseHandler.$directionFromCameraToMouse()/Math.PI));
			addDebugText(String.format("zoom %.2f", camera.$zoom()));
			addDebugText(" ");
		}
		
		// update state
		if(State.$currentState()!=null){
			State.$currentState().update(dt);
		}
		
		// update starfield
		// starfield must update last because of camera manipulation from state
		if(starfield!=null){
			for(StardustLayer stars:starfield){
				stars.update(dt);
			}
		}
		
	}

	public void render() {
		// clear screen
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		// render starfield
		if(renderbg && starfield!=null){
			for(StardustLayer stars:starfield){
				stars.render();
			}
		}
		
		// render game state
		if(State.$currentState()!=null){
			State.$currentState().render(camera);
		}
		
		// debug info
		if(showDebugText){
			for(int i=0; i<debugText.size(); i++){
				StringGraphics.drawUnifont(debugText.get(i), -this.$displayWidth()/2, (this.$displayHeight()/2)-4-(14*(debugText.size()-i)));
			}
			debugText.clear();
		}
		
	}

	public void destroy() {
		StringGraphics.destroy();
		TextureLoader.destroy();
		Display.destroy();
	}

	public boolean stopSignal() {
		return Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
	}

}
