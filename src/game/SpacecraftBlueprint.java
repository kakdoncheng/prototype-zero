package game;

import java.util.ArrayList;
import java.util.HashMap;

import engine.Attributes;
import engine.entities.Hardpoint;
import engine.entities.Hitbox;

public class SpacecraftBlueprint{
	
	// blueprints
	// for blueprints, attributes needs to represent empty hull,
	// i.e., without the list of outfits installed
	private static HashMap<String, SpacecraftBlueprint> blueprints;
	public static SpacecraftBlueprint $blueprint(String model){
		return blueprints.getOrDefault(model, null);
	}
	public static int registeredBlueprints(){
		return blueprints.size();
	}
	public static void registerBlueprint(String model, Attributes a, 
			HashMap<String, Integer> outfits, HashMap<String, Integer> cargo,
			ArrayList<Hitbox> hitboxes, ArrayList<Hardpoint> engineHardpoints, ArrayList<Hardpoint> weaponHardpoints){
		if(blueprints==null){
			blueprints=new HashMap<String, SpacecraftBlueprint>();
		}
		blueprints.put(model, new SpacecraftBlueprint(a, outfits, cargo, hitboxes, engineHardpoints, weaponHardpoints));
	}
		
	private Attributes a;
	private HashMap<String, Integer> b;
	private HashMap<String, Integer> c;
	private ArrayList<Hitbox> d;
	private ArrayList<Hardpoint> e;
	private ArrayList<Hardpoint> f;
	
	public Attributes $attributes(){
		return a;
	}
	public HashMap<String, Integer> $outfits(){
		return b;
	}
	public HashMap<String, Integer> $cargo(){
		return c;
	}
	public ArrayList<Hitbox> $hitboxes(){
		return d;
	}
	public ArrayList<Hardpoint> $engineHardpoints(){
		return e;
	}
	public ArrayList<Hardpoint> $weaponHardpoints(){
		return f;
	}
	
	public SpacecraftBlueprint(Attributes attributes, 
			HashMap<String, Integer> outfits, HashMap<String, Integer> cargo,
			ArrayList<Hitbox> hitboxes, ArrayList<Hardpoint> engineHardpoints, ArrayList<Hardpoint> weaponHardpoints){
		a=attributes;
		b=outfits;
		c=cargo;
		d=hitboxes;
		e=engineHardpoints;
		f=weaponHardpoints;
	}
}