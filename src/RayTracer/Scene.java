package RayTracer;
import java.util.ArrayList;

public class Scene {
	public ArrayList<Sphere> spheres;
	public ArrayList<Plane> planes;
	
	public Scene() {
		this.spheres = new ArrayList<>();
		this.planes = new ArrayList<>();
	}
	
	public void addSphere(Sphere s) {
		spheres.add(s);
	}
	
	public void addPlane(Plane p) {
		planes.add(p);
	}

}
