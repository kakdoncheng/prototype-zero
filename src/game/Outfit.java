package game;

import java.util.HashMap;

import engine.Attributes;

public class Outfit {
	
	private static HashMap<String, Attributes> registeredOutfits;
	public static void registerOutfit(String name, Attributes a){
		if(registeredOutfits==null){
			registeredOutfits=new HashMap<String, Attributes>();
		}
		registeredOutfits.put(name, a);
	}
	public static int registeredOutfits(){
		return registeredOutfits.size();
	}
	public static Attributes $attributesOf(String outfit){
		return registeredOutfits.getOrDefault(outfit, null);
	}

}
