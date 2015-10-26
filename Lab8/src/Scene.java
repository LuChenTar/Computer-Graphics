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
			double s =  1.0f * reflection.dot(r.direction.normalize());

			float value =(float) Math.max(Math.min((a + d + s), 1.0f), 0.0f);

//			return hit.color;

			Color finalColor = new Color((int) (hit.color.getRed() * value), (int) (hit.color.getGreen() *value),(int) (hit.color.getBlue()*value));

			return finalColor;

		} else {
			return background;
		}
//		Intersect inter
	}

}
