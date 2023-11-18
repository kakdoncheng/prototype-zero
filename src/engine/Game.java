package engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import engine.utils.PseudoRandomGenerator;


public abstract class Game {
	
	//prng
	protected PseudoRandomGenerator prng;
	public PseudoRandomGenerator $prng(){
		return prng;
	}
	public void setSeed(long seed){
		prng=new PseudoRandomGenerator(seed);
	}
	
	// display
	protected int width, height;
	protected String title;
	protected boolean active;
	protected boolean fullscreen;
	
	public boolean isActive(){
		return active;
	}
	public int $displayWidth(){
		return width;
	}
	public int $displayHeight(){
		return height;
	}
	public void setTitle(String title) {
	    this.title=title;
	}
	public void setResolution(int x, int y) {
		width=x;
		height=y;
	}
	public void setFullscreen(boolean b){
		fullscreen=b;
	}
	
	public void createDisplay(){
		try {                
			// set display mode
			try {
				// get modes
				DisplayMode[] dm=org.lwjgl.util.Display.getAvailableDisplayModes(
						width, height, -1, -1, -1, -1, 60, 60);
				//for(DisplayMode idm : dm){
				//	System.out.println(idm+" "+idm.isFullscreenCapable());
				//}
				
				// attempt to set display mode to correct resolution
				if(fullscreen){
					DisplayMode monitor=Display.getDesktopDisplayMode();
					width=monitor.getWidth();
					height=monitor.getHeight();
					//Display.setFullscreen(true);
					Display.setDisplayModeAndFullscreen(monitor);
				}else{
					org.lwjgl.util.Display.setDisplayMode(dm, new String[]{
							"width="+width,
							"height="+height,
							"bpp="+org.lwjgl.opengl.Display.getDisplayMode().getBitsPerPixel(),
							"freq="+60,
							});
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("engine.Game:ERROR: Unable to enter fullscreen, continuing in windowed mode.");
			}
			
			Display.setVSyncEnabled(true);
			//Display.setSwapInterval(1);
			//Display.sync(60);
			Display.create();
			if(Display.isCreated()) {
		    	Display.setTitle(title);
		    }
			//System.out.println(Display.getDisplayMode());
			
			// set icon
			//ByteBuffer[] list = new ByteBuffer[2];
			//list[0] = createBuffer(ImageIO.read(new File("./txr/icon16.png")));
			//list[1] = createBuffer(ImageIO.read(new File("./txr/icon32.png")));
			//Display.setIcon(list);
  
			// enable texture rendering to shapes
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// disable the OpenGL depth test since we're rendering 2D graphics
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			// allow for rendering transparent images
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			
			// origin in the middle for camera
			GL11.glOrtho(-width/2, width/2, height/2, -height/2, 1, -1);
			//GL11.glOrtho(0, width, height, 0, -1, 1);
			
		} catch (LWJGLException le) {
			le.printStackTrace();
		}
	}
	
	
	//FPS limit
	private int FPSLimit=60;
	private double runSpeed=1.0;
	private boolean fixedStep=false;
		
	//FPS calc
	private double cFPS=0,dFPS=0,avg=0,halfSec=0;
	private double exect=0;
	private int frames=0;
	public void setFPSLimit(int limit){
		FPSLimit=limit;
	}
	public void setRunSpeed(double speed){
		this.runSpeed=speed;
	}
	public void setFixedStep(boolean fixed){
		this.fixedStep=fixed;
	}
	public double $FPS(){
		return dFPS;
	}
	public double $runSpeed(){
		return runSpeed;
	}
	public double $currentSecond(){
		return halfSec;
	}
	public double $tickCapacity(){
		return exect*100;
	}
	
	public abstract void update(double dt);
	public abstract void render();
	public abstract void init();
	public abstract void destroy();
	
	public abstract boolean stopSignal();
	
	public void loop(){
		double l=System.nanoTime()/1000000000.0;
		while(active){
			double d=1.0/FPSLimit;
			double n=System.nanoTime()/1000000000.0;
			double dt=n-l;
			halfSec=n%1.0;
			if(dt>=d){
				double execl=System.nanoTime()/1000000000.0;
				update(fixedStep?runSpeed*d:runSpeed*dt);
				render();
				
				cFPS=1.0/(dt);
				if(frames>dFPS/5){
					//System.out.printf("%.1f\n", dFPS);
					exect=((System.nanoTime()/1000000000.0)-execl)/d;
					dFPS=avg/frames;
					//if(dFPS<FPSLimit-1){
					//	System.out.printf("engine.Game:WARNING: Significant frame rate drop below cap. (%.1f frames lost.)\n",
					//			FPSLimit-dFPS);
					//}
					avg=0;
					frames=0;
				}
				avg+=cFPS;
				frames++;
				l=n;
				//frame++;
				Display.update();
			}
			if(stopSignal()){
				destroy();
				active=false;
			}
		}
	}
	
	public Game(int width, int height, String title) {
		setResolution(width, height);
		setTitle(title);
		setSeed(System.currentTimeMillis());
	}
}
