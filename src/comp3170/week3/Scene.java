package comp3170.week3;

import comp3170.week3.Plane;

public class Scene {
	
	private Plane plane;
	
	public Scene() {
		
		plane = new Plane(-0.5f, 0.6f);
	}
	
	public void init() {
	}
	
	public void update(float deltaTime) {
		plane.update(deltaTime);
	}
	
	public void draw() {
		plane.draw();
	}
		

}
