package game;

import java.util.ArrayList;
import java.util.HashMap;

import engine.Attributes;
import engine.Vector;
import engine.entities.Hardpoint;
import engine.entities.Hitbox;
import engine.gfx.Camera;
import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class Spacecraft extends PrototypeEntity{
	
	// outfits
	private HashMap<String, Integer> outfits;
	public HashMap<String, Integer> $outfits(){
		return outfits;
	}
	// attempt to install/uninstall single instance of outfit
	// returns 0 on success
	// returns -1 if not enough outfit space
	// returns -2 if outfit not registered
	public int installOutfit(String outfit){
		Attributes oa=Outfit.$attributesOf(outfit);
		if(oa==null){
			return -2;
		}
		int installed=outfits.getOrDefault(outfit, 0);
		double remainingOutfitMass=maxOutfitMass-outfitMass;
		double installMass=oa.$value("mass");
		if(remainingOutfitMass>=installMass){
			outfitMass+=installMass;
			$attributes().addAttributes(oa);
			outfits.put(outfit, installed+1);
			return 0;
		}
		return -1;
	}
	public int uninstallOutfit(String outfit){
		int installed=outfits.getOrDefault(outfit, 0);
		if(installed>0){
			Attributes oa=Outfit.$attributesOf(outfit);
			if(oa==null){
				return -2;
			}
			double uninstallMass=oa.$value("mass");
			outfitMass-=uninstallMass;
			$attributes().subtractAttributes(oa);
			outfits.put(outfit, installed-1);
			return 0;
		}
		return -1;
	}
	
	// cargo
	private HashMap<String, Integer> cargo;
	public HashMap<String, Integer> $cargo(){
		return cargo;
	}
	// attempt to load an item into cargo hold
	// returns remaining cargo amount unable to be loaded
	public int loadCargo(String label, int amount){
		int existingCargo=cargo.getOrDefault(label, 0);
		double remainingCargoMass=maxCargoMass-cargoMass;
		double unitMass=1;
		Attributes oa=Outfit.$attributesOf(label);
		if(oa!=null){
			unitMass=oa.$value("mass");
		}
		int unitSpaceAvailable=(int)(remainingCargoMass/unitMass);
		int remainder=amount-unitSpaceAvailable;
		if(remainder<0){
			cargoMass+=amount*unitMass;
			cargo.put(label, existingCargo+amount);
			return 0;
		}else{
			int amountToLoad=(amount-remainder);
			cargoMass+=amountToLoad*unitMass;
			cargo.put(label, existingCargo+amountToLoad);
			return remainder;
		}
	}
	
	// instance variables
	// metadata
	private String name;
	private String model;
	private String category;
	
	// values
	private double mass;
	private double drag;
	private double outfitMass;
	private double cargoMass;
	private double maxOutfitMass;
	private double maxCargoMass;
	private double engineThrottle;
	private double engineThrust;
	private double engineReverseThrust;
	private double engineTurn;
	
	// mass
	// attribute mass value includes outfit mass,
	// be careful not to include such in calculations
	public double $totalMass(){
		return mass+cargoMass;
	}
	public double $outfitMass(){
		return outfitMass;
	}
	public double $cargoMass(){
		return cargoMass;
	}
	
	// engine calculations
	// set engine throttle
	public void setEngineThrottle(double factor){
		engineThrottle=factor;
		if(engineThrottle<0){
			engineThrottle=0;
		}
		if(engineThrottle>1){
			engineThrottle=1;
		}
	}
	public double $engineThrottle(){
		return engineThrottle;
	}
	
	// radian turns per second
	public double $turnRate(){
		return Math.PI*0.5*(engineTurn/$totalMass());
	}
	// friction force
	public double $nF(){
		return (0.5*this.$speed()*this.$speed()*drag)/$totalMass();
	}
	
	// apply external force,
	// with instant impulse?
	public void applyForce(double force, double direction, double dt){
		double fa=force/$totalMass();
		this.applyAccelerationVector(direction, fa, dt);
	}
	
	// textures
	private boolean renderThrust;
	private Texture ship;
	private Texture thruster;
	
	// hardpoints
	private ArrayList<Hardpoint> ehardpoints; // engine hardpoints
	private ArrayList<Hardpoint> whardpoints; // weapon hardpoints
	private ArrayList<String> weapons; // installed weapons
	
	// hitboxes
	private ArrayList<Hitbox> hitboxes;
	public ArrayList<Hitbox> $hitboxes(){
		return hitboxes;
	}
	
	// weapon groups
	private Weapon weaponGroup; 
	public void resetWeaponGroups(){
		
	}
	
	public void resetFromAttributes(){
		renderThrust=false;
		//ship=TextureLoader.$texture("prototype-voyager");
		//thruster=TextureLoader.$texture("prototype-thruster");
		ship=TextureLoader.$texture(this.$attributes().$metadata("texture-ship"));
		thruster=TextureLoader.$texture(this.$attributes().$metadata("texture-thrust"));
		
		this.name=this.$attributes().$metadata("name");
		this.model=this.$attributes().$metadata("model");
		this.category=this.$attributes().$metadata("category");
		if(name==null){
			this.name=this.model;
		}
		
		this.engineThrust=this.$attributes().$value("engine-thrust");
		this.engineReverseThrust=this.$attributes().$value("engine-reverse-thrust");
		this.engineTurn=this.$attributes().$value("engine-turn");
		this.mass=this.$attributes().$value("mass");
		this.drag=this.$attributes().$value("drag");
		this.maxOutfitMass=$attributes().$value("outfit-space");
		this.maxCargoMass=$attributes().$value("cargo-space");
		
		// sanity calculations
		this.$attributes().setValue("empty-turn-rate", Math.PI*0.5*(engineTurn/mass));
		this.$attributes().setValue("full-turn-rate", Math.PI*0.5*(engineTurn/(mass+maxCargoMass)));
		this.$attributes().setValue("empty-max-acceleration", engineThrust/mass);
		this.$attributes().setValue("full-max-acceleration", engineThrust/(mass+maxCargoMass));
		if(drag>0)this.$attributes().setValue("max-speed", Math.sqrt((engineThrust*2)/drag));
	}
	
	// controller
	private Controller controller;
	public void setController(Controller c){
		controller=c;
	}
	public Controller $controller(){
		return controller;
	}
	
	// controller variables
	private boolean thrustForward;
	private boolean thrustBackward;
	private boolean turnLeft;
	private boolean turnRight;
	private boolean firePrimary;
	public void thrustForward(){
		thrustForward=true;
	}
	public void thrustBackward(){
		thrustBackward=true;
	}
	public void turnLeft(){
		turnLeft=true;
	}
	public void turnRight(){
		turnRight=true;
	}
	public void firePrimary(){
		firePrimary=true;
	}
	public void resetControls(){
		thrustForward=false;
		thrustBackward=false;
		turnLeft=false;
		turnRight=false;
		firePrimary=false;
	}
	
	// constructor using attributes
	public Spacecraft(PrototypeGame game, SpacecraftBlueprint blueprint){
		this(game, blueprint.$attributes(), 
				blueprint.$outfits(), blueprint.$cargo(),
				blueprint.$hitboxes(), blueprint.$engineHardpoints(), blueprint.$weaponHardpoints());
	}
	public Spacecraft(PrototypeGame game, Attributes attributes, 
			HashMap<String, Integer> outfits, HashMap<String, Integer> cargo, 
			ArrayList<Hitbox> hitboxes, ArrayList<Hardpoint> engineHardpoints, ArrayList<Hardpoint> weaponHardpoints){
		super(game);
		this.attributes=attributes.clone();
		this.outfits=new HashMap<String, Integer>();
		this.cargo=new HashMap<String, Integer>();
		
		// load initial values
		this.outfitMass=0;
		this.cargoMass=0;
		resetFromAttributes();
		resetControls();
		
		// engine & weapon hardpoints
		// no need to clone hardpoints as they dont change
		weapons=new ArrayList<String>();
		ehardpoints=engineHardpoints;
		whardpoints=weaponHardpoints;
		setEngineThrottle(1);
		
		// hitboxes
		this.hitboxes=new ArrayList<Hitbox>();
		for(Hitbox hb:hitboxes){
			Hitbox nhb=hb.clone();
			nhb.setOwner(this);
			this.hitboxes.add(nhb);
		}
		
		// install outfits & check for weapons
		// for each outfit found
		for(HashMap.Entry<String, Integer> outfit:outfits.entrySet()){
			String label=outfit.getKey();
			Attributes oa=Outfit.$attributesOf(label);
			int amount=outfit.getValue();
			// handle unregistered outfits
			if(oa==null){
				continue;
			}
			
			// install each instance of outfit
			boolean isWeapon=oa.$metadata("weapon-type")!=null;
			for(int i=0; i<amount; i++){
				// make sure to check for unsafe installs
				int errorCode=installOutfit(label);
				if(errorCode>-1){
					// on successful install
					// add to weapons to install if weapon
					if(isWeapon){
						weapons.add(label);
					}
				}else{
					// add outfit to cargo on unsuccessful install
					cargo.put(label, cargo.getOrDefault(label, 0)+1);
				}
			}
		}
		
		// load specified cargo
		for(HashMap.Entry<String, Integer> cargoUnit:cargo.entrySet()){
			loadCargo(cargoUnit.getKey(), cargoUnit.getValue());
		}
		
		// reload values after installing outfits
		resetFromAttributes();
		
		// install weapons & reset weapon group
		// weapon groups will eventually call attributes from registered list, similar to outfits
		// will need to generate new groups each time weapons have changed
		// WIP
		weaponGroup=new Weapon("20mm M84A2S Vulcan");
		weaponGroup=new Weapon("Prototype Blaster");
		for(Hardpoint h:whardpoints){
			weaponGroup.addHardpoint(h);
		}
		
		// install turrets?
	}
	
	public void update(double dt) {
		// check if still active
		// set speed vectors?
		
		// update turrets
		// fire turrets
		
		// fire weapon groups
		weaponGroup.update(dt);
		if (firePrimary){
			weaponGroup.fire(game, this);
		}
		
		// calculate forces
		double turnRate=$turnRate();
		double totalMass=$totalMass();
		double fa=(engineThrust*engineThrottle)/totalMass;
		double rfa=engineReverseThrust/totalMass;
		double nF=$nF();
		
		// attempt to turn counter-clockwise
		if (turnLeft){
			this.setDirection(this.$t()-(turnRate*dt));
			//this.$attributes().setValue("actual-turn", Math.toDegrees(Math.PI*0.5*cca));
		}
		// attempt to turn clockwise
		if (turnRight){
			this.setDirection(this.$t()+(turnRate*dt));
			//this.$attributes().setValue("actual-turn", Math.toDegrees(Math.PI*0.5*cca));
		}
		// constrain direction to [0, Math.PI*2]
		this.setDirection(Vector.constrainTheta(this.$t()));
		
		// attempt to thrust forward
		if (thrustForward){
			this.applyAccelerationVector(this.$t(), fa, dt);
			renderThrust=true;
			
			// test effects
			// for each engine hardpoint, add sparks
			/*
			for(Hardpoint hp:ehardpoints){
				// test sparks
				int amt=game.$prng().$int(-12, 3);
				for(int i=0;i<amt;i++){
					int sx=(int)(x+hp.$xOffsetFrom(t));
					int sy=(int)(y+hp.$yOffsetFrom(t));
					
					// projectiles should do the same
					Spark s=new Spark(game, sx, sy, t-Math.PI);
					s.addSpeedVector($speedt(), $speed());
					game.$currentState().addParticle(s);
				}
			}
			//*/
		}
		// attempt to thrust backward/turn opposite current speed direction
		if (thrustBackward){
			if(engineReverseThrust>0){
				this.applyAccelerationVector(this.$t()+Math.PI, rfa, dt);
			}else if(!turnLeft && !turnRight){
				rotateTowards($speedt()+Math.PI,turnRate,dt);
			}
			
		}
		
		// friction force
		// nF = 0.5 * ship speed^2 * ship drag
		this.applyAccelerationVector(this.$speedt()-Math.PI, nF, dt);
		
		// update position
		this.updatePosition(dt);
		
		// debug attribute values
		//game.addDebugText("thrustForward "+thrustForward);
		//game.addDebugText("firePrimary "+firePrimary);
		
		// update heat
		// check for boarding ship
		// check for landing
		
		resetControls();
	}

	public void render(Camera c) {
		
		// fade out away from stardust bounds
		// see astronomical object
		///*
		double bounds=4096*0.825;//(4096*0.825)+radius;
		double distance=Vector.distanceFromTo(x, y, c.$dx(), c.$dy());
		double alpha=1;
		if(distance-bounds>0){
			double factor=(distance-bounds)/(bounds*0.5);
			alpha=1-factor;
		}
		
		// render engine thrust
		// hardpoint offset should be built into hardpoints
		if (renderThrust && $speed()>0){
			// for each engine hardpoint, render thrust
			for(Hardpoint hp:ehardpoints){
				double es=0.75; // render scale
				double rso=game.$prng().$double(0.8, 1); // random scale offset

				int erx=(int)c.$cx(x+hp.$xOffsetFrom(t));
				int ery=(int)c.$cy(y+hp.$yOffsetFrom(t));
				thruster.render(erx, ery, Math.toDegrees(t)+180, c.$zoom()*es*rso, 1, 1, 1, 0.75*alpha);
			}
			renderThrust=false;
		}
		
		// render weapons
		//weaponGroup.render(this, c);
		
		// render ship
		ship.render(c.$cx(x), c.$cy(y), Math.toDegrees(t)+180, c.$zoom(), 1, 1, 1, alpha);
		
		// render turrets
		
	}
	
	public String toString(){
		return String.format("%s (%s)", name, category);
	}

	public void onDeath() {
		
	}
}
