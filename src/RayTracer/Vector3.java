package RayTracer;

public class Vector3 {
	public float x;
	public float y;
	public float z;
	
	public Vector3() {}
	
	public Vector3(Vector3 v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}
	
	public Vector3(float v) {
		x = v;
		y = v;
		z = v;
	}
	
	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vector3 neg(Vector3 v) {
		return new Vector3(-v.x, -v.y, -v.z);
	}
	
	public static Vector3 add(Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public static Vector3 sub(Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	
	public static Vector3 mul(Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
	}
	
	public static Vector3 div(Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x / v2.x, v1.y / v2.y, v1.z / v2.z);
	}
	
	public static Vector3 scale(Vector3 v, float a) {
		return new Vector3(v.x * a, v.y * a, v.z * a);
	}
	
	public static float length(Vector3 v) {
		return (float)Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	}
	
	public static Vector3 normalOf(Vector3 v) {
		float l = length(v);
		if(l != 0) {
			return new Vector3(v.x / l, v.y / l, v.z / l);
		}else {
			return new Vector3();
		}
	}
	
	public static Vector3 cross(Vector3 v1, Vector3 v2) {
		return new Vector3(v1.y * v2.z - v1.z * v2.y,
						   v1.z * v2.x - v1.x * v2.z,
						   v1.x * v2.y - v1.y * v2.x);
	}
	
	public static float dot(Vector3 v1, Vector3 v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	public static Vector3 lerp(Vector3 v1, Vector3 v2, float t) {
		return new Vector3(v1.x + (v2.x - v1.x) * t, v1.y + (v2.y - v1.y) * t, v1.z + (v2.z - v1.z) * t);
	}
}
