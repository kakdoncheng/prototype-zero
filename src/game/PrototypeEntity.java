package game;

import engine.Attributes;
import engine.entities.Entity;

public abstract class PrototypeEntity extends Entity{

	protected PrototypeGame game;
	protected Attributes attributes;
	
	public void setAttributes(Attributes a){
		this.attributes=a;
	}
	public Attributes $attributes(){
		return attributes;
	}
	
	public void updatePosition(double dt){
		x+=game.convertMetersToPixels(dx)*dt;
		y+=game.convertMetersToPixels(dy)*dt;
	}
	
	public PrototypeEntity(PrototypeGame game){
		this(game, new Attributes());
	}
	public PrototypeEntity(PrototypeGame game, Attributes attributes) {
		super(game);
		this.game=game;
		this.attributes=attributes;
	}

}
