package RayTracer;

import static RayTracer.Vector3.*;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class RayTracer {
	private int width = 1280;
	private int height = 720;
	
	private int raysPerPixel = 32;
	
	private JFrame frame;
	private JLabel display;
	
	private BufferedImage imageBuffer;
	
	public RayTracer() {
		frame = new JFrame();
		display = new JLabel();
		imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		display.setPreferredSize(new Dimension(width, height));
		frame.add(display);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);  
		display.setText("Generating Image. Please Stand by...");
		frame.setSize(width, height);
		frame.setVisible(true);
		
		float pixelWidth = 1.0f / width;
		float pixelHeight = 1.0f / height;
		
		int pixelsCompleted = 0;
		int totalPixels = width * height;
		float percentComplete = 0;
		
		Scene scene = generateRandomScene();
//		Plane plane = new Plane(new Material(new Vector3(0.0f), new Vector3(0.5f)),
//				                new Vector3(0, 0, 1), 0);
//		Sphere sphere = new Sphere(new Material(new Vector3(0.0f), new Vector3(0.7f, 0.5f, 0.3f), 1.0f), 
//				                   new Vector3(0, 0, 0), 1);
//		Sphere sphere2 = new Sphere(new Material(new Vector3(0.0f), new Vector3(0.7f), 0.5f), 
//                new Vector3(3, -2, 0), 1);
//		Sphere sphere3 = new Sphere(new Material(new Vector3(0.0f), new Vector3(0.2f, 0.8f, 0.2f), 0.1f), 
//                new Vector3(-2, -1, 2), 1);
//		scene.addPlane(plane);
//		scene.addSphere(sphere);
//		scene.addSphere(sphere2);
//		scene.addSphere(sphere3);
		
		Vector3 cameraP = new Vector3(0, -10, 1);
		Vector3 cameraZ = normalOf(cameraP);
		Vector3 cameraX = normalOf(cross(new Vector3(0, 0, 1), cameraZ));
		Vector3 cameraY = normalOf(cross(cameraZ, cameraX));
		
		float filmDist = 1.0f;
		float filmW = 1.0f;
		float filmH = 1.0f;
		if(width > height) {
			filmH = (float)height / (float)width; 
		}else if(height > width) {
			filmW = (float)width / (float)height; 
		}
		float hFilmW = 0.5f * filmW;
		float hFilmH = 0.5f * filmH;
		Vector3 filmCenter = Vector3.sub(cameraP, Vector3.scale(cameraZ, filmDist));
		
		int index = 0;
		byte[] buffer = ((DataBufferByte)imageBuffer.getRaster().getDataBuffer()).getData();
		for (int y = 0; y < height; y++) {
			float filmY = -1.0f + 2.0f * ((float)(height - y) / (float)height);
			for(int x = 0; x < width; x++) {
				float filmX = -1.0f + 2.0f * ((float)x / (float)width);
				Vector3 color = new Vector3();
				float colorWeight = 1.0f / (float)raysPerPixel;
				for(int ray = 0; ray < raysPerPixel; ray++) {
					float xOffset = filmX + ((((float)Math.random() * 2) - 1) * pixelWidth);
					float yOffset = filmY + ((((float)Math.random() * 2) - 1) * pixelHeight);
					Vector3 filmP = add(filmCenter, add(scale(cameraX, xOffset * hFilmW), scale(cameraY, yOffset * hFilmH)));
					Vector3 rayOrigin = cameraP;
					Vector3 rayDirection =  normalOf(sub(filmP, cameraP));
					color = add(color, scale(rayCast(scene, rayOrigin, rayDirection), colorWeight));
				}
				
				buffer[index++] = (byte)(color.z * 255);
				buffer[index++] = (byte)(color.y * 255);
				buffer[index++] = (byte)(color.x * 255);	
			}
			pixelsCompleted += width;
			percentComplete = ((float)pixelsCompleted / (float)totalPixels) * 100.0f;
			display.setText("Generating Image: " + percentComplete +"% complete");
		}
		display.setIcon(new ImageIcon(imageBuffer));
		frame.pack();
	}
	
	private Vector3 rayCast(Scene scene, Vector3 rayOrigin, Vector3 rayDirection) {
		Vector3 skyColor = new Vector3(0.3f, 0.4f, 0.5f);
		Vector3 result = new Vector3(0.0f);
		Vector3 attenuation = new Vector3(1);
		
		float hitDistance = Float.MAX_VALUE;
		float minHitDistance = 0.0001f;
		float tolerance = 0.0001f;
		
		for (int bounce = 0; bounce < 8; bounce++) {
			Material currentHitMaterial = null;
			Vector3 nextNormal = null;
			
			for(Plane p : scene.planes) {
				float denom = dot(p.normal, rayDirection);
				if((denom < -tolerance) && !(denom > tolerance)) {
					float t = (-p.size - dot(p.normal, rayOrigin)) / denom;
					if(t > minHitDistance && t < hitDistance) {
						hitDistance = t;
						currentHitMaterial = p.material;
						nextNormal = new Vector3(p.normal);
					}
				}
			}
			
			for(Sphere s : scene.spheres) {
				Vector3 sphereSurfaceHit = sub(rayOrigin, s.position);
				float a = dot(rayDirection, rayDirection);
				float b = 2.0f * dot(rayDirection, sphereSurfaceHit);
				float c = dot(sphereSurfaceHit, sphereSurfaceHit) - s.radius * s.radius;
				float denom = 2.0f * a;
				float rootTerm = (float)Math.sqrt(b * b - 4.0f * a * c);
				if(rootTerm > tolerance) {
					float tp = (-b + rootTerm) / denom;
					float tn = (-b - rootTerm) / denom;
					float t = tp;
					if(tn > minHitDistance && tn < tp) {
						t = tn;
					}
					
					if(t > minHitDistance && t < hitDistance) {
						hitDistance = t;
						currentHitMaterial = s.material;
						
						nextNormal = normalOf(add(scale(rayDirection, t), sphereSurfaceHit));
					}
				}
			}
			if(currentHitMaterial != null) {
				result = add(result, mul(attenuation, currentHitMaterial.emissionColor));
				float cosAtt = dot(neg(rayDirection), nextNormal);
				if(cosAtt < 0) cosAtt = 0;
				attenuation = mul(attenuation, scale(currentHitMaterial.reflectiveColor, cosAtt));
				rayOrigin = add(rayOrigin, scale(rayDirection, hitDistance));//nextOrigin;
				Vector3 rayBounce = sub(rayDirection, scale(nextNormal, 2.0f * dot(rayDirection, nextNormal)));
				float r1 = -1.0f + 2.0f * (float)Math.random();
				float r2 = -1.0f + 2.0f * (float)Math.random();
				float r3 = -1.0f + 2.0f * (float)Math.random();
				Vector3 bounceRandomness = normalOf(add(nextNormal, new Vector3(r1, r2, r3)));
				rayDirection = lerp(bounceRandomness, rayBounce, currentHitMaterial.diffuse);
			}else {
				result = add(result, mul(attenuation, skyColor));
				break;
			}
		}
		
		return result;
	}
	
	private Scene generateRandomScene() {
		Scene scene = new Scene();
		Random rgen = new Random();
		Vector3 ec = new Vector3(0.1f);
		Vector3 rc = new Vector3(rgen.nextFloat(), rgen.nextFloat(), rgen.nextFloat());
		float dif = rgen.nextFloat();
		Material m = new Material(ec, rc, dif);
		Plane plane = new Plane(m, new Vector3(0, 0, 1), 1f);
		scene.addPlane(plane);
		int totalSpheres = rgen.nextInt(10) + 5;
		for(int i = 0; i < totalSpheres; i++) {
			float x = rgen.nextFloat() * 10 - 5;
			float y = rgen.nextFloat() * 6;
			float z = rgen.nextFloat() * 3f - 0.5f;
			Vector3 pos = new Vector3(x, y, z);
			x = rgen.nextFloat() * 0.2f;
			y = rgen.nextFloat() * 0.2f;
			z = rgen.nextFloat() * 0.2f;
			ec = new Vector3(x, y, z);
			rc = new Vector3(rgen.nextFloat(), rgen.nextFloat(), rgen.nextFloat());
			dif = rgen.nextFloat();
			m = new Material(ec, rc, dif);
			float rad = rgen.nextFloat();
			Sphere s = new Sphere(m, pos, rad);
			scene.addSphere(s);
		}
		
		return scene;
	}
	
	public static void main(String[] args) {
		new RayTracer();
	}
}
