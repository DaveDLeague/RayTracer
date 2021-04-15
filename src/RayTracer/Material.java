package RayTracer;

public class Material {
	Vector3 emissionColor;
	Vector3 reflectiveColor;
	float diffuse;
	
	public Material() {
		this(new Vector3(), new Vector3(), 0);
	}
	
	public Material(Vector3 e, Vector3 r) {
		this(e, r, 0);
	}
	public Material(Vector3 e, Vector3 r, float d) {
		this.emissionColor = e;
		this.reflectiveColor = r;
		this.diffuse = d;
	}
}
