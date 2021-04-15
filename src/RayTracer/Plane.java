package RayTracer;

public class Plane {
	public Material material;
	public Vector3 normal;
	public float size;
	
	public Plane(Material m, Vector3 n, float s) {
		this.material = m;
		this.normal = n;
		this.size = s;
	}
}
