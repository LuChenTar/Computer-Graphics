import java.awt.Color;
import java.util.ArrayList;

/**
 * Scene Eric McCreath 2009
 */

public class Scene extends ArrayList<Sphere> {

	Color background = Color.black;
	Color ambient = new Color(0.2f,0.2f,0.2f);
	Color lightColour= new Color(0.8f,0.8f,0.8f);
	Color specularColour= new Color(1.0f,1.0f,1.0f);

	public Color raytrace(Ray r) {
		Sphere hit = null;
		Double mindis = null;

		// store the light source direction
		 P3D lightingPosition = new P3D(-0.7, -0.7, 0.8);

		for (Sphere s : this) {
			Double t = s.intersect(r);
			if (t != null) {
				if (mindis == null || t < mindis) {
					mindis = t;
					hit = s;
				}
			}
		}
		if (hit != null) {
			Double t = hit.intersect(r);
			P3D intersetPoint = r.position.add(r.direction.scale(t));
			P3D normal = hit.center.sub(intersetPoint).normalize();

			P3D reflection = normal.scale(2 * (lightingPosition.dot(normal))).sub(lightingPosition).normalize();

			double a =  0.2f;
			double d =  0.8f * lightingPosition.normalize().dot(normal);
			double s =  1.0f * Math.pow(reflection.dot(r.direction.normalize()), 50.0);

			float redvalue =(float) Math.max(Math.min(((a + d) * hit.color.getRed()  + 255.0f * s), 255.0f), 0.0f);
			float greenvalue =(float) Math.max(Math.min(((a + d) * hit.color.getGreen()  + 255.0f * s), 255.0f), 0.0f);
			float bluevalue =(float) Math.max(Math.min(((a + d) * hit.color.getBlue()  + 255.0f * s), 255.0f), 0.0f);

			Color finalColor = new Color((int) redvalue, (int) greenvalue ,(int) bluevalue);

			return finalColor;

		} else {
			return background;
		}
//		Intersect inter
	}

}
