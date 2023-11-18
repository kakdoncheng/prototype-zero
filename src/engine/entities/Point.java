package engine.entities;

import engine.Game;
import engine.gfx.Camera;

public class Point extends Entity{

	public Point(Game game, double x, double y) {
		super(game);
		setXY(x, y);
	}

	public void update(double dt) {
		return;
	}

	public void render(Camera c) {
		return;
	}

	public void onDeath() {
		return;
	}

	public boolean isCollidable(){
		return false;
	}
}
