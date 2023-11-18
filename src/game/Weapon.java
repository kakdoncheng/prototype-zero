package game;

import java.util.ArrayList;

import engine.Attributes;
import engine.entities.Hardpoint;
import engine.gfx.Camera;
import engine.gfx.Texture;
import engine.gfx.TextureLoader;

public class Weapon {
	
	// weapon timer
	private int index; // hardpoint index
	private double timer; // time since last shot
	private double rps; // rounds per second
	private double dtps; // time per shot
	
	// weapon attributes
	//private int ammo;
	//private int ammoCapacity;
	//private String ammoType;
	//private Texture texture;
	
	private double accuracy;
	private double heat;
	private double energy;
	private double windUp;
	private double windUpHeat;
	private double windUpEnergy;
	private double recoilForce;
	
	// projectile attributes
	private double damage;
	private double heatDamage;
	private double energyDamage;
	private double armorPenetration;
	private double speed;
	private double range;
	private double hitForce;
	
	private Texture textureProjectile;
	private String defaultColor;
	private String diffuseColor;
	
	// projectile base color
	private double pRed;
	private double pGreen;
	private double pBlue;
	
	// projectile diffuse color
	private double dRed;
	private double dGreen;
	private double dBlue;
	
	public void loadFromAttributes(Attributes a){
		// attributes
		//this.ammo=(int)a.$value("ammo");
		//this.ammoCapacity=(int)a.$value("ammo-capacity");
		//this.ammoType=a.$metadata("ammo-type");
		//this.texture=TextureLoader.$texture(a.$metadata("texture"));
		
		this.accuracy=a.$value("accuracy");
		// moa -> radians π/10800
		accuracy=(accuracy*Math.PI)/10800;
		accuracy/=2;
		
		this.heat=a.$value("heat");
		this.energy=a.$value("energy");
		this.windUp=a.$value("wind-up");
		this.windUpHeat=a.$value("wind-up-heat");
		this.windUpEnergy=a.$value("wind-up-energy");
		this.recoilForce=a.$value("recoil-force");
		
		// projectile attributes
		this.damage=a.$value("damage");
		this.heatDamage=a.$value("heat-damage");
		this.energyDamage=a.$value("energy-damage");
		this.armorPenetration=a.$value("armor-penetration");
		this.speed=a.$value("speed");
		this.range=a.$value("range");
		this.hitForce=a.$value("hit-force");
		
		this.textureProjectile=TextureLoader.$texture(a.$metadata("texture-projectile"));
		this.defaultColor=a.$metadata("projectile-color");
		this.diffuseColor=a.$metadata("diffuse-color");
		
		// parse projectile color
		if(defaultColor!=null){
			int pColor=Integer.parseInt(defaultColor, 16);
			this.pRed=((pColor&0xFF0000)>>16)/255.0;
			this.pGreen=((pColor&0xFF00)>>8)/255.0;
			this.pBlue=(pColor&0xFF)/255.0;
		}else{
			this.pRed=0;
			this.pGreen=0;
			this.pBlue=0;
		}
		
		// parse diffuse color
		if(diffuseColor!=null){
			int dColor=Integer.parseInt(diffuseColor, 16);
			this.dRed=((dColor&0xFF0000)>>16)/255.0;
			this.dGreen=((dColor&0xFF00)>>8)/255.0;
			this.dBlue=(dColor&0xFF)/255.0;
		}else{
			this.dRed=0;
			this.dGreen=0;
			this.dBlue=0;
		}
		
		// timer
		hardpoints=new ArrayList<Hardpoint>();
		index=0;
		timer=0;
		rps=a.$value("rate-of-fire");
		dtps=1/rps;
	}

	
	private ArrayList<Hardpoint> hardpoints;
	public void addHardpoint(Hardpoint hp){
		hardpoints.add(hp);
	}
	
	public Weapon(String model){
		WeaponBlueprint wbp=WeaponBlueprint.$blueprint(model);
		loadFromAttributes(wbp.$attributes());
	}
	
	public void update(double dt) {
		timer+=dt;
	}
	
	public void fire(PrototypeGame game, Spacecraft owner){
		double rate=dtps/hardpoints.size();
		if(timer>rate){
			timer=rate;
		}
		if(timer>=rate){
			// projectile
			double ot=owner.$t();
			double ppx=owner.$x()+hardpoints.get(index).$xOffsetFrom(ot);
			double ppy=owner.$y()+hardpoints.get(index).$yOffsetFrom(ot);
			double ppt=ot+hardpoints.get(index).$tOffset();
			Projectile pp=new Projectile(game, owner, textureProjectile, ppx, ppy, ppt+game.$prng().$double(-accuracy, accuracy),
					damage, heatDamage, energyDamage, armorPenetration, hitForce, speed, range, 
					pRed, pBlue, pGreen, dRed, dGreen, dBlue);
			pp.addSpeedVector(owner.$speedt(), owner.$speed());
			game.$currentState().addProjectile(pp);
			
			// recoil
			owner.applyForce(recoilForce, ppt+Math.PI, 1);
			
			timer-=rate;
			index++;
			index%=hardpoints.size();
		}
	}
	
	public void render(PrototypeEntity owner, Camera c){
		/*
		for(Hardpoint hp:hardpoints){
			int erx=(int)c.$cx(owner.$x()+hp.$xOffsetFrom(owner.$t()));
			int ery=(int)c.$cy(owner.$y()+hp.$yOffsetFrom(owner.$t()));
			gun.render(erx, ery, Math.toDegrees(owner.$t())+180, c.$zoom()*scale);
		}
		//*/
	}
}
