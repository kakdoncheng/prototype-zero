package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import engine.Vector;
import engine.entities.Hitbox;
import engine.gfx.Camera;
import engine.gfx.StringGraphics;
import engine.input.MouseHandler;

public class Controller {
	
	// controlled ship
	private PrototypeGame game;
	private Minimap map;
	private Spacecraft ship;
	
	// attach/detach from ship
	public void attachTo(Spacecraft ship){
		if(this.ship!=null){
			// controller can control only one ship at a time
			// code is commented out as an intended bug to test faction colors
			//this.ship.setController(null);
		}
		this.ship=ship;
		this.ship.setController(this);
	}
	public void detach(){
		this.ship.setController(null);
		this.ship=null;
	}
	
	// targets
	//private Spacecraft lastAggressorShip;
	private Spacecraft mouseOverShip;
	private Spacecraft targetedShip;
	private AstronomicalObject mouseOverPlanet;
	private AstronomicalObject targetedPlanet;
	
	// mouse HUD render sets
	private HashSet<Spacecraft> targetedShips;
	private HashSet<AstronomicalObject> targetedPlanets;
	
	// mouse timeout
	private double lmx; // last mx/my
	private double lmy;
	private double timer;
	private double timeout;
	
	// mouse controls
	private double thrustRadiusMin;
	private double thrustRadiusMax;
	private double thrustWarmup;
	private double thrustAngle;
	
	// color swizzle
	private double red;
	private double green;
	private double blue;
	
	// control flags
	private boolean toggleKey;
	private boolean toggleMouse;
	private boolean mouseControlsEnabled;
	private boolean mouseCombatEnabled;
	private boolean keyboardControlsEnabled;
	private boolean renderMinimap;
	private boolean renderDebugIndicators;
	
	// control hints
	private boolean showHints;
	private boolean showStatus;
	private double showStatusTimer;
	private double showStatusTimeout;
	private String status;
	private ArrayList<String> hints;
	
	private void setStatus(String label){
		this.status=label;
		this.showStatusTimer=0;
	}
	
	
	public Controller(PrototypeGame game, double maxRenderBounds){
		this.game=game;
		this.map=new Minimap(game, maxRenderBounds);
		this.lmx=MouseHandler.$ax();
		this.lmy=MouseHandler.$ay();
		this.timer=0;
		this.timeout=4;
		
		//this.lastAggressorShip=null;
		this.mouseOverShip=null;
		this.mouseOverPlanet=null;
		this.targetedShip=null;
		this.targetedPlanet=null;
		this.targetedShips=new HashSet<Spacecraft>();
		this.targetedPlanets=new HashSet<AstronomicalObject>();
		
		this.ship=null;
		this.toggleKey=false;
		this.toggleMouse=false;
		this.keyboardControlsEnabled=true;
		this.mouseControlsEnabled=true;
		this.renderMinimap=true;
		this.renderDebugIndicators=false;
		
		this.showHints=false;
		this.hints=new ArrayList<String>();
		this.showStatus=true;
		this.showStatusTimeout=4;
		setStatus("Press [TAB] to view debug mode.");
		
		this.red=1;
		this.green=0;
		this.blue=0;
		
		this.thrustAngle=Math.PI/8;
		this.thrustRadiusMin=120;
		this.thrustRadiusMax=840;
		this.thrustWarmup=0;
	}

	// will switch to taking a AstronomicalObjectSystem object as parameter
	// instead of all the system components individually
	// or grab current system from main game state/spacecraft
	public void update(ArrayList<Spacecraft> ships,
			ArrayList<Projectile> projectiles,
			ArrayList<AstronomicalObject> planets, double dt){
		
		// stop focusing on target if inactive
		// or if controlled ship is targeted
		// or wrecked?
		if(targetedShip!=null && (!targetedShip.isActive() || targetedShip.equals(ship))){
			targetedShip=null;
		}
		
		// determine mouse/object intersections
		// reset mouse intersections first
		targetedShips.clear();
		targetedPlanets.clear();
		mouseOverShip=null;
		mouseOverPlanet=null;
		
		// check for mouse/spacecraft/planet intersections
		// ignore controlled ship
		// only store the first object that intersects mouse
		// only check if mouse enabled & not timed out
		if(mouseControlsEnabled && timer<=timeout){
			for(Spacecraft ship:ships){
				if(this.ship.equals(ship)){
					continue;
				}
				double x=ship.$x();
				double y=ship.$y();
				double radius=32;
				double dxm=Vector.distanceFromTo(x, y, MouseHandler.$mx(), MouseHandler.$my());
				if(dxm<radius){
					mouseOverShip=ship;
					break;
				}
			}
			for(AstronomicalObject ao:planets){
				double x=ao.$x();
				double y=ao.$y();
				double radius=ao.$radius();
				double dxm=Vector.distanceFromTo(x, y, MouseHandler.$mx(), MouseHandler.$my());
				if(dxm<radius){
					mouseOverPlanet=ao;
					break;
				}
			}
			
			// left-click will set mouseOverShip as new target
			if(Mouse.isButtonDown(0)){
				if(mouseOverShip!=null && mouseOverShip!=targetedShip){
					targetedShip=mouseOverShip;
					setStatus("Targeting spacecraft "+mouseOverShip+".");
				}
			}
			
			// middle-click
			if(Mouse.isButtonDown(2)){
				if(!toggleMouse){
					// quick stance switch
					mouseCombatEnabled=!mouseCombatEnabled;
					if(mouseCombatEnabled){
						setStatus("Switching to combat mode.");
					}else{
						setStatus("Switching to interaction mode.");
					}
					toggleMouse=true;
				}
			}else{
				toggleMouse=false;
			}
		}
		
		// add objects to list for HUD render
		targetedShips.add(targetedShip);
		targetedShips.add(mouseOverShip);
		targetedPlanets.add(targetedPlanet);
		targetedPlanets.add(mouseOverPlanet);
		
		// toggle flags
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB)){
			if(!toggleKey){
				renderDebugIndicators=!renderDebugIndicators;
				showHints=!showHints;
				game.showDebugText(renderDebugIndicators);
				toggleKey=true;
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_M)){
			if(!toggleKey){
				mouseControlsEnabled=!mouseControlsEnabled;
				toggleKey=true;
			}
		}else if(Keyboard.isKeyDown(Keyboard.KEY_C)){
			if(!toggleKey){
				if(targetedShip!=null){
					attachTo(targetedShip);
					setStatus("Attaching controller to "+ship+".");
					toggleKey=true;
				}else if(mouseOverShip!=null){
					attachTo(mouseOverShip);
					setStatus("Attaching controller to "+ship+".");
					toggleKey=true;
				}
			}
		}else{
			toggleKey=false;
		}
		
		// toggle control hints
		if(showHints){
			hints.add("Toggle [M]ouse Controls");
			hints.add("[C]ontrol Targeted Ship");
			hints.add(" ");
		}
				
		// control camera
		// scroll wheel zoom
		int dmw=Mouse.getDWheel();
		if (dmw<0 || Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
			game.$camera().changeZoom(-dt*4);
		}
		if (dmw>0 || Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
			game.$camera().changeZoom(+dt*4);
		}
		game.$camera().updateZoom(dt);
		
		// arrows move camera
		/*
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			game.$camera().dxy(-dt*120, 0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			game.$camera().dxy(dt*120, 0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)){
			game.$camera().dxy(0, -dt*120);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			game.$camera().dxy(0, dt*120);
		}
		//*/
		
		// control ship
		if(ship!=null){
			
			// keyboard
			if(keyboardControlsEnabled){
				if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
					ship.setEngineThrottle(1);
					ship.thrustForward();
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
					ship.turnLeft();
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
					ship.thrustBackward();
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
					ship.turnRight();
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
					ship.firePrimary();
				}
				
				// control hints
				if(showHints){
					hints.add("Zoom [-/+]");
					hints.add("Thrust Forward [UP]");
					hints.add("Thrust Backwards / Turn Opposite [DOWN]");
					hints.add("Turn Left [LEFT]");
					hints.add("Turn Right [RIGHT]");
					hints.add("Fire Primary [SPACE]");
					hints.add(" ");
				}
			}
			
			// mouse controls
			if(mouseControlsEnabled){
				// ai likely very similar to mouse controls,
				// for player, mouse is target
				double targetx=MouseHandler.$mx();
				double targety=MouseHandler.$my();
				// direction to target
				double tm=Vector.directionFromTo(ship.$x(), ship.$y(), targetx, targety);
				// distance to target
				double dxm=Vector.distanceFromTo(ship.$x(), ship.$y(), targetx, targety);
				// tdistance from ship t to target direction
				double tdxm=Vector.tdistanceFromTo(ship.$t(), tm);
				
				// rotate ship towards target
				double dturn=ship.$turnRate()*dt;
				double tError=0.018;
				if(tdxm>tError || tdxm<-tError){
					if(tdxm<-dturn){
						ship.turnLeft();
					}else if(tdxm>dturn){
						ship.turnRight();
					}else{
						ship.setDirection(tm);
					}
				}
				
				// handle right-click
				// thrust towards target
				if(Mouse.isButtonDown(1)){
					thrustWarmup+=dt*2;
					if(thrustWarmup>1){
						thrustWarmup=1;
					}
					if(tdxm>-thrustAngle && tdxm<thrustAngle){
						double factor=(dxm-thrustRadiusMin)/(thrustRadiusMax-thrustRadiusMin);
						factor*=thrustWarmup;
						if(factor<0.025){
							factor=0.025;
						}
						ship.setEngineThrottle(factor);
						ship.thrustForward();
					}
				}else{
					thrustWarmup=0;
				}
				
				// handle left-click
				if(mouseCombatEnabled){
					// fire primary weapons
					if(Mouse.isButtonDown(0)){
						ship.firePrimary();
					}
				}else{
					
				}
				
				
				// debug text
				//game.addDebugText("tdx-mouse: "+tdxm);
				
				// control hints
				if(showHints){
					hints.add("Zoom (Scroll)");
					hints.add("Fire Primary (Left-Click)");
					hints.add("Turn / Strafe (Right-Click)");
					hints.add(" ");
				}
			}
			
			// center camera
			game.$camera().hardCenterOnEntity(ship);
			//game.$camera().centerOnEntity(ship, dt);
		}
		
		// handle mouse timeout
		timer+=dt;
		boolean mouseButtonClicked=false;
		for(int i=0; i<3; i++){
			mouseButtonClicked=mouseButtonClicked || Mouse.isButtonDown(i);
		}
		if(mouseButtonClicked || MouseHandler.$ax()!=lmx || MouseHandler.$ay()!=lmy){
			timer=0;
		}
		this.lmx=MouseHandler.$ax();
		this.lmy=MouseHandler.$ay();
		
		// handle status label timeout
		showStatusTimer+=dt;
		if(showStatusTimer>=showStatusTimeout){
			status=null;
		}
		
		
		// debug text
		if(renderDebugIndicators){
			// no of enitites
			game.addDebugText(String.format("particles: %d", game.$currentState().$particleCount()));
			game.addDebugText(" ");
			
			// ship diagnostics
			//*
			game.addDebugText(String.format("model: %s", ship.$attributes().$metadata("model")));
			game.addDebugText(String.format("total-mass: %.1f kg", ship.$totalMass()));
			game.addDebugText(String.format("drag: %.1f", ship.$attributes().$value("drag")));
			game.addDebugText(String.format("direction: %.2fπ", ship.$t()/Math.PI));
			game.addDebugText(String.format("speed-direction: %.2fπ", ship.$speedt()/Math.PI));
			game.addDebugText(String.format("throttle: %.1f%%", ship.$engineThrottle()*100));
			game.addDebugText(String.format("speed: %.1f m/s", ship.$speed()));
			game.addDebugText(String.format("max-speed: %.1f m/s", ship.$attributes().$value("max-speed")));
			double loA=ship.$attributes().$value("full-max-acceleration");
			double hiA=ship.$attributes().$value("empty-max-acceleration");
			if(loA<hiA){
				game.addDebugText(String.format("max-acceleration: %.1f-%.1f m/s^2", loA, hiA));
			}else{
				game.addDebugText(String.format("max-acceleration: %.1f m/s^2", hiA));
			}
			double loTR=Math.toDegrees(ship.$attributes().$value("full-turn-rate"));
			double hiTR=Math.toDegrees(ship.$attributes().$value("empty-turn-rate"));
			if(loTR<hiTR){
				game.addDebugText(String.format("turn-rate: %.1f-%.1f deg/s", loTR, hiTR));
			}else{
				game.addDebugText(String.format("turn-rate: %.1f deg/s", hiTR));
			}
			
			//*/
			
			// dump raw ship attributes
			/*
			game.addDebugText(" ");
			for(String s:ship.$attributes().toStringList()){
				game.addDebugText(s);
			}
			//*/
			
			// outfits
			//*
			HashMap<String, Integer> outfits=ship.$outfits();
			if(outfits.size()>0){
				game.addDebugText(" ");
				game.addDebugText(String.format("Outfits: (%.1f/%.1f kg)", ship.$outfitMass(), ship.$attributes().$value("outfit-space")));
				for(HashMap.Entry<String, Integer> outfit:outfits.entrySet()){
					game.addDebugText(String.format("%s, %d", outfit.getKey(), outfit.getValue()));
				}
			}
			//*/
			
			// cargo
			//*
			HashMap<String, Integer> cargoHold=ship.$cargo();
			if(cargoHold.size()>0){
				game.addDebugText(" ");
				game.addDebugText(String.format("Cargo: (%.1f/%.1f kg)", ship.$cargoMass(), ship.$attributes().$value("cargo-space")));
				for(HashMap.Entry<String, Integer> cargo:cargoHold.entrySet()){
					game.addDebugText(String.format("%s, %d", cargo.getKey(), cargo.getValue()));
				}
			}
			//*/
			
			/*
			for(int i=0; i<Mouse.getButtonCount(); i++){
				game.addDebugText(Mouse.getButtonName(i)+" "+Mouse.isButtonDown(i));
			}
			//ArrayList<String> info=ship.$attributes().toStringList();
			//*/
		}
	}
	
	public void render(ArrayList<Spacecraft> ships,
			ArrayList<Projectile> projectiles,
			ArrayList<AstronomicalObject> planets, Camera c){
		
		// control hints/status
		if(showHints){
			for(int i=0; i<hints.size(); i++){
				String hint=hints.get(i);
				StringGraphics.drawUnifont(hint, (game.$displayWidth()/2)-(hint.length()*9), (game.$displayHeight()/2)-4-(14*(hints.size()-i)), 1, 1, 1, 0.5, 1);
			}
			hints.clear();
		}
		if(showStatus && status!=null){
			StringGraphics.drawUnifont(status, (game.$displayWidth()/2)-(status.length()*9), (game.$displayHeight()/2)-18, 1, 0, 0, 1, 1);
		}
		
		// radar indicators
		if(renderDebugIndicators){// && c.$zoom()<=game.$minZoom()){
			// thrust radius
			if(thrustRadiusMin>0){
				renderRadiusIndicator(this.ship.$x(), this.ship.$y(), thrustRadiusMin, c, red, green, blue, 0.25);
			}
			if(thrustRadiusMax>0){
				renderRadiusIndicator(this.ship.$x(), this.ship.$y(), thrustRadiusMax, c, red, green, blue, 0.25);
			}
			// hitboxes
			for(Spacecraft ship:ships){
				for(Hitbox h:ship.$hitboxes()){
					renderRadiusIndicator(h.$x(), h.$y(), h.$radius(), c, red, green, blue, 1);
				}
			}
			for(Projectile p:projectiles){
				renderTracerIndicator(p.$lx(), p.$ly(), p.$x(), p.$y(), c, red, green, blue, 1);
			}
			for(AstronomicalObject ao:planets){
				double x=ao.$x();
				double y=ao.$y();
				double radius=ao.$radius();
				renderRadiusIndicator(x, y, radius, c, red, green, blue, 1);
			}
		}
		
		// render targeted object overlays
		for(Spacecraft ship:targetedShips){
			renderTargetedIndicator(ship, c, 0.5, 0.5, 0.5, 1);
		}
		for(AstronomicalObject ao:targetedPlanets){
			renderTargetedIndicator(ao, c, 0.5, 0.5, 0.5, 1);
		}
		
		// render minimap
		if(renderMinimap){
			if(targetedShip!=null){
				mouseOverShip=targetedShip;
			}
			map.render(ship, targetedShips, targetedPlanets, ships, projectiles, planets, c);
		}
		
		// render cursor
		// only render if mouse not timed out
		if(mouseControlsEnabled && timer<=timeout){
			if(renderDebugIndicators){
				if(mouseCombatEnabled){
					renderCrosshairCursor(6, 12, 0, 0);
				}else{
					renderCrosshairCursor(0, 6, 0, 0);
				}
			}else{
				if(mouseCombatEnabled){
					renderCrosshairCursor(6, 12, 0, 0);
				}else{
					renderDotCursor();
				}
				
			}
		}
		
	}
	
	// helper render methods
	// cursors
	private void renderDotCursor(){
		int seg=16;
		int rs=16;
		double ci=2*Math.PI;
		double cis=ci/seg;
		double amx=MouseHandler.$ax();
		double amy=MouseHandler.$ay();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1, 0, 0, 0.5);
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,rs);
			double dxyy1=Vector.vectorToDy(i,rs); 
			double dxyx2=Vector.vectorToDx(i+(ci/seg),rs);
			double dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
			GL11.glVertex2d(amx+dxyx1, amy+dxyy1);
			GL11.glVertex2d(amx+dxyx2, amy+dxyy2);
		}
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1, 0, 0, 1);
		GL11.glVertex2d(amx-1, amy-1);
		GL11.glVertex2d(amx+1, amy+1);
		GL11.glVertex2d(amx-1, amy+1);
		GL11.glVertex2d(amx+1, amy-1);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	private void renderCrosshairCursor(int a, int b, int c, int d){
		double amx=MouseHandler.$ax();
		double amy=MouseHandler.$ay();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(1,0,0,1);
		GL11.glVertex2d(amx-a+c, amy-a-d);
		GL11.glVertex2d(amx-b+c, amy-b-d);
		
		GL11.glVertex2d(amx+a-c, amy+a+d);
		GL11.glVertex2d(amx+b-c, amy+b+d);
		
		GL11.glVertex2d(amx-a+c, amy+a+d);
		GL11.glVertex2d(amx-b+c, amy+b+d);
		
		GL11.glVertex2d(amx+a-c, amy-a-d);
		GL11.glVertex2d(amx+b-c, amy-b-d);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	// indicators
	// radius indicator
	private void renderTracerIndicator(double x1, double y1, double x2, double y2, Camera c, double r, double g, double b, double a){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(r, g, b, a);
		GL11.glVertex2d(c.$cx(x1), c.$cy(y1));
		GL11.glVertex2d(c.$cx(x2), c.$cy(y2));
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	private void renderRadiusIndicator(double x, double y, double radius, Camera c, double r, double g, double b, double a){
		int seg=32;
		int rs=(int)(radius);
		double ci=2*Math.PI;
		double cis=ci/seg;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor4d(r, g, b, a);
		for(double i=0;i<ci;i+=cis){
			double dxyx1=Vector.vectorToDx(i,rs);
			double dxyy1=Vector.vectorToDy(i,rs); 
			double dxyx2=Vector.vectorToDx(i+(ci/seg),rs);
			double dxyy2=Vector.vectorToDy(i+(ci/seg),rs);
			GL11.glVertex2d(c.$cx(x+dxyx1), c.$cy(y+dxyy1));
			GL11.glVertex2d(c.$cx(x+dxyx2), c.$cy(y+dxyy2));
		}
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	// target indicator spacecraft
	private void renderTargetedIndicator(Spacecraft ship, Camera c, double r, double g, double b, double a){
		if(ship==null){
			return;
		}
		double x=ship.$x();
		double y=ship.$y();
		double radius=32;
		
		// distance to camera
		double dxc=Vector.distanceFromTo(x, y, c.$dx(), c.$dy());
			
		// if target off-screen, render arrow indicator
		// otherwise render four point indicator
		double cx=c.$cx(x);
		double cy=c.$cy(y);
		double hgw=game.$displayWidth()/2;
		double hgh=game.$displayHeight()/2;
		boolean outOfBounds=false;
		if(cx<-hgw){
			cx=-hgw;
			outOfBounds=true;
		}else if(cx>hgw){
			cx=hgw;
			outOfBounds=true;
		}
		if(cy<-hgh){
			cy=-hgh;
			outOfBounds=true;
		}else if(cy>hgh){
			cy=hgh;
			outOfBounds=true;
		}
		if(outOfBounds){
			// arrow indicator
			double tt=Vector.directionFromTo(x, y, c.$dx(), c.$dy());
			double rr=8;
			double pad=4;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslated(cx, cy, 0);
			GL11.glRotated(Math.toDegrees(tt), 0, 0, 1);
			GL11.glColor4d(r, g, b, a);
			GL11.glBegin(GL11.GL_TRIANGLES);
			
			GL11.glVertex2d(0, pad);
			GL11.glVertex2d(-rr*0.33, rr+pad);
			GL11.glVertex2d(rr*0.33, rr+pad);
			
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// distance in m
			String dxs=String.format("%.1f", game.convertPixelsToMeters(dxc));
			double sr=rr+pad+4;
			double sx=cx-Vector.vectorToDx(-tt, sr);
			double sy=cy+Vector.vectorToDy(-tt, sr);
			double dxsw=dxs.length()*9;
			double dxsh=14.0;
			if(sx>0){
				sx-=dxsw;
			}
			if(sy>0){
				sy-=dxsh;
			}
			StringGraphics.drawUnifont(dxs, (int)sx, (int)sy, 0.5, 0.5, 0.5, 1, 1);
		}else{
			// render four point indicator
			double rr=8;
			double pad=16;
			for(int i=0; i<360; i+=90){
				double ii=i+45;//+Math.toDegrees(t);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glPushMatrix();
				GL11.glTranslated(c.$cx(x+Vector.vectorToDx(Math.toRadians(ii), radius+pad)), 
						c.$cy(y+Vector.vectorToDy(Math.toRadians(ii), radius+pad)), 0);
				GL11.glRotated(ii, 0, 0, 1);
				GL11.glColor4d(r, g, b, a);
				GL11.glBegin(GL11.GL_TRIANGLES);
				
				GL11.glVertex2d(0, 0);
				GL11.glVertex2d(-rr*0.33, rr);
				GL11.glVertex2d(rr*0.33, rr);
				
				GL11.glEnd();
				GL11.glPopMatrix();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			
			// render text for four point indicator
			double tpad=(radius+pad)*c.$zoom()+8;
			double tdir=Math.toRadians(-45);
			StringGraphics.drawUnifont(String.format("%.1f", game.convertPixelsToMeters(dxc)),
					c.$cx(x)+(int)Vector.vectorToDx(tdir, tpad)+2,
					c.$cy(y)+(int)Vector.vectorToDy(tdir, tpad), 
					r, g, b, a, 1);
		}
	}
	// target indicator planet
	private void renderTargetedIndicator(AstronomicalObject ao, Camera c, double r, double g, double b, double a){
		if(ao==null){
			return;
		}
		String label=ao.$label();
		double x=ao.$x();
		double y=ao.$y();
		double radius=ao.$radius();
		
		// distance to camera
		double dxc=Vector.distanceFromTo(x, y, c.$dx(), c.$dy());
		
		// render five point indicator
		double rr=8;
		double pad=16;
		for(int i=0; i<360; i+=360/5){
			double ii=i+36;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPushMatrix();
			GL11.glTranslated(c.$cx(x+Vector.vectorToDx(Math.toRadians(ii), radius+pad)), 
					c.$cy(y+Vector.vectorToDy(Math.toRadians(ii), radius+pad)), 0);
			GL11.glRotated(ii, 0, 0, 1);
			GL11.glColor4d(r, g, b, a);
			GL11.glBegin(GL11.GL_TRIANGLES);
			
			GL11.glVertex2d(0, 0);
			GL11.glVertex2d(-rr*0.33, rr);
			GL11.glVertex2d(rr*0.33, rr);
			
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		// render text for five point indicator
		double tpad=(radius+pad)*c.$zoom()+24;
		double tdir=Math.toRadians(-36);
		StringGraphics.drawUnifont(label, 
				c.$cx(x)+(int)Vector.vectorToDx(tdir, tpad)+8,
				c.$cy(y)+(int)Vector.vectorToDy(tdir, tpad), 
				r, g, b, a, 2);
		StringGraphics.drawUnifont(String.format("%.1f", game.convertPixelsToMeters(dxc)),
				c.$cx(x)+(int)Vector.vectorToDx(tdir, tpad)+2,
				c.$cy(y)+(int)Vector.vectorToDy(tdir, tpad)+22, 
				r, g, b, a, 1);
	}
}
