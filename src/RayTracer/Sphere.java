package RayTracer;

public class Sphere {
	public Material material;
	public Vector3 position;
	public float radius;
	
	public Sphere(Material m, Vector3 p, float r) {
		this.material = m;
		this.position = p;
		this.radius = r;
	}
}
