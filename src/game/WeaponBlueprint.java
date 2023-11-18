package game;

import java.util.HashMap;

import engine.Attributes;

public class WeaponBlueprint {
	
	private static HashMap<String, WeaponBlueprint> blueprints;
	public static WeaponBlueprint $blueprint(String model){
		return blueprints.getOrDefault(model, null);
	}
	public static int registeredBlueprints(){
		return blueprints.size();
	}
	public static void registerBlueprint(String model, Attributes a){
		if(blueprints==null){
			blueprints=new HashMap<String, WeaponBlueprint>();
		}
		
		// debug
		for(String l:a.toStringList()){
			System.out.println(l);
		}
		
		blueprints.put(model, new WeaponBlueprint(a));
	}
	
	private Attributes a;
	
	public Attributes $attributes(){
		return a;
	}
	
	public WeaponBlueprint(Attributes attributes){
		this.a=attributes;
	}

}
