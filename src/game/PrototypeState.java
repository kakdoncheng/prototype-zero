package game;

import java.util.ArrayList;

import engine.State;
import engine.entities.Entity;
import engine.entities.EntityCollection;
import engine.gfx.Camera;

public class PrototypeState extends State{
	
	// test bounds, equal to STARDUST_BOUNDS
	private int bounds=4096;
	private PrototypeGame game;
	
	// particles
	private EntityCollection<Entity> particles;
	public void addParticle(Entity e){
		particles.addEntity(e);
	}
	public int $particleCount(){
		return particles.$size();
	}
	
	// projectiles
	private EntityCollection<Projectile> projectiles;
	public void addProjectile(Projectile e){
		projectiles.addEntity(e);
	}
	
	// test spaceships
	private Controller player;
	private EntityCollection<Spacecraft> ships;
	public void addShip(Spacecraft e){
		ships.addEntity(e);
	}
	public EntityCollection<Spacecraft> $ships(){
		return ships;
	}
	
	// test planets
	private ArrayList<AstronomicalObject> planets;
	
	public PrototypeState(PrototypeGame game){
		this.game=game;
	}

	public void reset() {
		// bound radius based on screen size
		double w2=game.$displayWidth()*game.$displayWidth();
		double h2=game.$displayHeight()*game.$displayHeight();
		int maxRenderBounds=(int)(Math.sqrt(w2+h2)/(2*game.$minZoom()));
		
		// test ships
		player=new Controller(game, maxRenderBounds);
		ships=new EntityCollection<Spacecraft>();
		ships.setRenderDistance(maxRenderBounds);
		
		// player ship
		Spacecraft ship=new Spacecraft(game, SpacecraftBlueprint.$blueprint("Prototype Zero"));
		ships.addEntity(ship);
		player.attachTo(ship);
		
		// dummy ships
		ArrayList<String> dummies=new ArrayList<String>();
		dummies.add("Space Shuttle");
		dummies.add("Model 2405");
		dummies.add("Modified MONO-C");
		dummies.add("Prototype Voyager");
		for(String s:dummies){
			ship=new Spacecraft(game, SpacecraftBlueprint.$blueprint(s));
			ship.offsetTR(game.$prng().$double(0, Math.PI*2), game.$prng().$double(320, 1960));
			ship.setDirection(game.$prng().$double(0, Math.PI*2));
			ships.addEntity(ship);
		}
		
		particles=new EntityCollection<Entity>();
		particles.setRenderDistance(bounds);
		projectiles=new EntityCollection<Projectile>();
		projectiles.setRenderDistance(maxRenderBounds);
		
		// test planets
		planets=new ArrayList<AstronomicalObject>();
		planets.add(new AstronomicalObject(0, 0, 59.5*5, 5, "sokol-1", "Sokol-1"));
		planets.add(new AstronomicalObject(1280, 720, 64, 1, "earth", "Lunarius Terra"));
	}

	public void update(double dt) {
		
		// test ship
		ships.update(dt);
		player.update(ships.$entities(), projectiles.$entities(), planets, dt);
		
		particles.update(dt);
		projectiles.update(dt);
		
		// test planet gravity force
		/*
		double cG=6.674e-11; // gravitational constant
		double dist=Vector.distanceFromTo(ship.$x(), ship.$y(), 0, 0)-pr;
		if(dist<1){
			dist=1;
		}
		double gF=cG*((ship.$attributes().$value("mass")*1000*6e24)/Math.pow(dist*10e6,2));
		gF=100;
		ship.$attributes().setValue("gF", gF);
		ship.applyAccelerationVector(Vector.directionFromTo(ship.$x(), ship.$y(), 0, 0), gF, dt);
		//*/
	}

	public void render(Camera c) {
		// objects
		for(AstronomicalObject ao:planets){
			ao.render(c);
		}
		ships.render(c);
		//if(c.$zoom()>game.$minZoom()){
			particles.render(c);
		//}
		projectiles.render(c);
		
		// controller
		player.render(ships.$entities(), projectiles.$entities(), planets, c);
	}

}
