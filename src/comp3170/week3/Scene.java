package comp3170.week3;

import comp3170.week3.Plane;

public class Scene {
	
	private Jetplane plane;
	
	public Scene() {
		
		plane = new Jetplane(1000, 0.05f, 1f);
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
