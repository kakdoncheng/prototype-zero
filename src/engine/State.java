package engine;

import java.util.HashMap;

import engine.gfx.Camera;

public abstract class State {
	
	private static HashMap<Integer, State> states;
	private static int currentState;
	
	public static void addState(int id, State e){
		if(states==null){
			states=new HashMap<Integer, State>();
			currentState=0;
		}
		states.put(id, e);
	}
	public static void setCurrentState(int id){
		currentState=id;
	}
	public static State $state(int key){
		return states.get(key);
	}
	public static State $currentState(){
		return states.get(currentState);
	}
	
	public abstract void reset();
	public abstract void update(double dt);
	public abstract void render(Camera c);
}