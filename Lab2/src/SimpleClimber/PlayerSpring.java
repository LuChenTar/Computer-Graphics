package SimpleClimber;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class PlayerSpring implements MouseMotionListener, KeyListener {

	/**
	 * Little SimpleClimber.Wall Rock Climbing Copyright 2009 Eric McCreath GNU LGPL
	 */

	ArrayList<Spring> springs;
	ArrayList<Joint> joints;
	Points points;
	Points velocity;
	Points forces;

	XYPoint lhf, rhf, lff, rff, bf;

	Wall wall;
	Point lastpoint;
	boolean leftaction, rightaction, leftfaction, rightfaction, bodyaction;

	static final double height = 1.85;
	static final double torso = 1.0;

	XYPoint lh, le, ls, n, p, ln, lf, rh, re, rs, rn, rf;
	Spring lRad, lHum, lClav, rRad, rHum, rClav, Lum1, Lum2, lFem, lTib, rFem,
			rTib;

	//coordinate for chalk bag
	XYPoint lus, rus, lbs, rbs, lbb;

	Timer t;

	//dt: pre-set time unit
	double angleAccel = 0, angleVelocity = 0, x = 0, dt = 0.1;
	double distance = 0;
	double angle = Math.PI / 4;

	public PlayerSpring(Wall w) {
		this.wall = w;
		points = new Points();
		points.add(lh = new XYPoint(-0.5, 2.0));
		points.add(le = new XYPoint(-0.3, 1.7));
		points.add(ls = new XYPoint(-0.2, 1.58));

		points.add(rh = new XYPoint(0.5, 2.0));
		points.add(re = new XYPoint(0.3, 1.7));
		points.add(rs = new XYPoint(0.2, 1.58));

		points.add(n = new XYPoint(0.0, 1.58));
		points.add(p = new XYPoint(0.0, 1.0));

		points.add(rn = new XYPoint(0.2, 0.7));
		points.add(rf = new XYPoint(0.2, 0.2));

		points.add(ln = new XYPoint(-0.2, 0.7));
		points.add(lf = new XYPoint(-0.2, 0.2));

		points.add(lus = new XYPoint(n.getX() - 0.1, n.getY() - 0.08));
		points.add(rus = new XYPoint(n.getX() - 0.1, n.getY()));
		points.add(lbs = new XYPoint(n.getX() + 0.1, n.getY() - 0.08));
		points.add(rbs = new XYPoint(n.getX() + 0.1, n.getY()));
		points.add(lbb = new XYPoint(n.getX() - 0.30, n.getY() - 0.40));

		double k = 0.4;

		springs = new ArrayList<Spring>();

		springs.add(lClav = new Spring(0.2, k, n, ls, points));
		springs.add(lHum = new Spring(0.3, k, ls, le, points));
		springs.add(lRad = new Spring(0.33, k, le, lh, points));

		springs.add(rClav = new Spring(0.2, k, n, rs, points));
		springs.add(rHum = new Spring(0.3, k, rs, re, points));
		springs.add(rRad = new Spring(0.33, k, re, rh, points));

		springs.add(Lum1 = new Spring(0.58, k, n, p, points));
		springs.add(Lum2 = new Spring(0.58, k, p, n, points));

		springs.add(lFem = new Spring(0.44, k, p, ln, points));
		springs.add(lTib = new Spring(0.5, k, ln, lf, points));

		springs.add(rFem = new Spring(0.44, k, p, rn, points));
		springs.add(rTib = new Spring(0.5, k, rn, rf, points));

		joints = new ArrayList<Joint>();
		joints.add(new Joint(Lum2, lClav, Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(Lum2, rClav, -Math.PI / 2.0, -Math.PI / 2.0));

		joints.add(new Joint(rClav, rHum, -Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(rHum, rRad, 0.0, Math.PI / 2.0));

		joints.add(new Joint(lClav, lHum, -Math.PI / 2.0, Math.PI / 2.0));
		joints.add(new Joint(lHum, lRad, -Math.PI / 2.0, 0.0));

		joints.add(new Joint(Lum1, rFem, 0.1, 0.4));
		joints.add(new Joint(rFem, rTib, -0.2, 0.1));

		joints.add(new Joint(Lum1, lFem, -0.4, -0.1));
		joints.add(new Joint(lFem, lTib, -0.1, 0.2));

		points.translate(new XYPoint(5.0, 1.0));

		forces = new Points(points.size());
		velocity = new Points(points.size());
		velocity.zero();

		lhf = new XYPoint(0.0, 0.0);
		rhf = new XYPoint(0.0, 0.0);
		lff = new XYPoint(0.0, 0.0);
		rff = new XYPoint(0.0, 0.0);
		bf = new XYPoint(0.0, 0.0);

		leftaction = false;

		for (Spring s : springs) {
			XYPoint p1 = points.get(s.p1index);
			XYPoint p2 = points.get(s.p2index);
			XYPoint dif1 = s.p1distance().scale(0.6);
			XYPoint dif2 = s.p1distance().scale(-0.3);
			p2.translate(dif1);
			p1.translate(dif2);
		}

		for (Joint j : joints) {
			XYPoint v1 = j.s1.vector();
			XYPoint v2 = j.s2.vector();

			double angle = v1.angle(v2);

			if (angle < j.mina) {
				XYPoint v2r = v2.rotate(-(angle - j.mina));
				points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r));
			} else if (angle > j.maxa) {
				XYPoint v2r = v2.rotate((j.maxa - angle));
				points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r));
			}
		}

		t =new Timer(200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chalkupdate();
			}
		});
	}

	public void draw(Graphics2D of) {

		of.setTransform(wall.trans);
		of.setColor(Color.black);
		of.setStroke(new BasicStroke(0.01f));

		// This is the section of code you need to modify to draw a more
		// realistic climber.

		/**
		*
		*Description: visually improving the climber using simple line
		*
		*@author Lu Chen
		*/
		//hair and beard
		of.setPaint(Color.BLACK);
		of.fill(new Ellipse2D.Double(n.getX() - 0.25, n.getY() + 0.10, 0.5, 0.5));
		of.setStroke(new BasicStroke(0.03f));
		of.draw(new Line2D.Double(n.getX() - 0.15, n.getY() + 0.5, n.getX() - 0.25, n.getY() + 0.55));
		of.draw(new Line2D.Double(n.getX(), n.getY() + 0.5, n.getX(), n.getY() + 0.67));
		of.draw(new Line2D.Double(n.getX() + 0.15, n.getY() + 0.5, n.getX() + 0.25, n.getY() + 0.55));

		//face shape
		of.setStroke(new BasicStroke(0.01f));
		of.setPaint(Color.WHITE);
		of.fill(new Ellipse2D.Double(n.getX() - 0.248, n.getY() + 0.15, 0.49, 0.4));

		//eyes
		of.setPaint(Color.BLACK);
		of.fill(new Ellipse2D.Double(n.getX() - 0.18, n.getY() + 0.33, 0.1, 0.1));
		of.fill(new Ellipse2D.Double(n.getX() + 0.03, n.getY() + 0.33, 0.1, 0.1));

		//mouth
		of.setStroke(new BasicStroke(0.03f));
		of.draw(new Line2D.Double(n.getX() - 0.08, n.getY() + 0.23, n.getX() + 0.06, n.getY() + 0.23));
		of.setStroke(new BasicStroke(0.01f));

		//hands
		of.setPaint(Color.PINK);
		of.fill(new Ellipse2D.Double(lh.getX() - 0.11, lh.getY() + 0.01, 0.15, 0.15));
		of.fill(new Ellipse2D.Double(rh.getX() - 0.04, rh.getY(), 0.15, 0.15));

		//fingers
		of.setStroke(new BasicStroke(0.02f));
		of.draw(new Line2D.Double(lh.getX() - 0.13, lh.getY() + 0.06, lh.getX() - 0.17, lh.getY() + 0.08));
		of.draw(new Line2D.Double(lh.getX() - 0.10, lh.getY() + 0.16, lh.getX() - 0.12, lh.getY() + 0.20));
		of.draw(new Line2D.Double(lh.getX(), lh.getY() + 0.17, lh.getX() + 0.01, lh.getY() + 0.22));
		of.draw(new Line2D.Double(lh.getX() + 0.05, lh.getY() + 0.10, lh.getX() + 0.09, lh.getY() + 0.14));

		of.draw(new Line2D.Double(rh.getX() - 0.05, rh.getY() + 0.06, rh.getX() - 0.09, rh.getY() + 0.08));
		of.draw(new Line2D.Double(rh.getX() - 0.02, rh.getY() + 0.16, rh.getX() - 0.04, rh.getY() + 0.20));
		of.draw(new Line2D.Double(rh.getX() + 0.08, rh.getY() + 0.17, rh.getX() + 0.10, rh.getY() + 0.22));
		of.draw(new Line2D.Double(rh.getX() + 0.14, rh.getY() + 0.07, rh.getX() + 0.19, rh.getY() + 0.10));

		//a harness
		of.setStroke(new BasicStroke(0.025f));
		of.setPaint(Color.BLACK);
		of.draw(new Ellipse2D.Double(p.getX() - 0.05, p.getY() + 0.3, 0.10, 0.02));
		of.fill(new Ellipse2D.Double(2.43, 5.68, 0.2, 0.2));
		of.draw(new QuadCurve2D.Double(p.getX() - 0.05, p.getY() + 0.3, 2.8, 1, 2.5, 5.8));


		//chalk bag
		of.setStroke(new BasicStroke(0.03f));
		of.setPaint(new Color(208, 192, 174));

		of.draw(new Line2D.Double(lus.getX(), lus.getY(), rus.getX(), rus.getY()));
		of.draw(new Line2D.Double(lbs.getX(), lbs.getY(), rbs.getX(), rbs.getY()));
		of.draw(new RoundRectangle2D.Double(lbb.getX(), lbb.getY(), 0.60, 0.32, 0.1, 0.1));

		//shoes
		of.setPaint(new Color(90, 131, 162));
		of.fill(new Ellipse2D.Double(lf.getX() - 0.2, lf.getY() - 0.1, 0.2, 0.1));
		of.fill(new Ellipse2D.Double(rf.getX(), rf.getY() - 0.1, 0.2, 0.1));

		of.setPaint(Color.BLACK);
		of.setStroke(new BasicStroke(0.018f));
		for (Spring s : springs) {
			s.draw(of);
		}

		//forearms
		of.setColor(Color.red);
		of.setStroke(new BasicStroke(0.05f));
		lRad.draw(of);
		rRad.draw(of);

		//arms
		of.setColor(Color.red);
		of.setStroke(new BasicStroke(0.07f));
		lHum.draw(of);
		rHum.draw(of);

		//leg
		of.setStroke(new BasicStroke(0.06f));
		of.setColor(Color.blue);
		lTib.draw(of);
		rTib.draw(of);

		//thigh
		of.setStroke(new BasicStroke(0.08f));
		of.setColor(Color.blue);
		lFem.draw(of);
		rFem.draw(of);

	}

	public void chalkupdate() {
		x= lbb.getX();
		angleAccel = - 9.81 / 0.5 * Math.sin(angle);
		angleVelocity += angleAccel * dt;
		angle += angleVelocity * dt;
//		System.out.println("I am speed" + angleVelocity);
//		System.out.println("I am distance" + distance);
		x += Math.sin(angle) * 0.05;
		lbb.set(new XYPoint(x, lbb.getY()));
	}

	public void update(final GameComponent canvas, Wall wall) {
		/*
		 * forces.zero(); for (SimpleClimber.XYPoint f : forces) { f.translate(new
		 * SimpleClimber.XYPoint(0.0,-0.001)); }
		 * 
		 * forces.get(points.indexOf(lh)).translate(lhf); lhf.zero();
		 * 
		 * // spring force for (SimpleClimber.Spring s : springs) {
		 * forces.get(s.p1index).translate(s.p1force());
		 * forces.get(s.p2index).translate(s.p2force()); }
		 * 
		 * // damperner for (int i = 0; i< springs.size(); i++) { SimpleClimber.Spring s =
		 * springs.get(i); SimpleClimber.XYPoint v1 = forces.get(s.p1index); SimpleClimber.XYPoint v2 =
		 * forces.get(s.p2index); SimpleClimber.XYPoint vel1 = velocity.get(s.p1index);
		 * SimpleClimber.XYPoint vel2 = velocity.get(s.p2index);
		 * 
		 * SimpleClimber.XYPoint dif = vel1.sub(vel2); SimpleClimber.XYPoint v1d = dif.scale(0.1); SimpleClimber.XYPoint
		 * v2d = dif.scale(-0.1); v1.translate(v2d); v2.translate(v1d); }
		 * 
		 * 
		 * for (int i = 0; i< velocity.size(); i++) {
		 * velocity.get(i).translate(forces.get(i)); }
		 * 
		 * 
		 * 
		 * 
		 * for (int i = 0; i< points.size(); i++) { SimpleClimber.XYPoint p = points.get(i);
		 * p.translate(velocity.get(i)); if (p.y < 0.2) { p.y = 0.2; } }
		 */

		if (lhf.length() > 0.0) {
			movelim(lhf, lh, le, ls, lRad, lHum);
		}

		if (rhf.length() > 0.0) {
			movelim(rhf, rh, re, rs, rRad, rHum);
		}

		if (lff.length() > 0.0) {
			movelim(lff, lf, ln, p, lFem, lTib);
		}

		if (rff.length() > 0.0) {
			movelim(rff, rf, rn, p, rFem, rTib);
		}

		if (bf.length() > 0.0) {
			if (canmovelim(bf, rh, re, rs, rRad, rHum)
					&& canmovelim(bf, lf, ln, p, lFem, lTib)
					&& canmovelim(bf, lh, le, ls, lRad, lHum)
					&& canmovelim(bf, rf, rn, p, rFem, rTib)) {
				domovelim(bf, rh, re, rs, rRad, rHum);
				domovelim(bf, lf, ln, p, lFem, lTib);
				domovelim(bf, lh, le, ls, lRad, lHum);
				domovelim(bf, rf, rn, p, rFem, rTib);
				p.translate(bf);
				n.translate(bf);
				/**
				*
				*Description: move the chalk bag while body is moving
				*
				*@author Lu Chen
				*/
				lbb.translate(bf);
				lbs.translate(bf);
				rbs.translate(bf);
				lus.translate(bf);
				rus.translate(bf);
				ls.translate(bf);
				rs.translate(bf);
				bf.zero();
//				stop the swing after 7 seconds
				Timer timer =new Timer(7000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						t.stop();
//					reset chalk bag to default
						lbb.set(new XYPoint(n.getX() - 0.30, lbb.getY()));
					}
				});
				timer.start();
			}

		}

		/*
		 * // reset the lengths for (SimpleClimber.Spring s : springs) { SimpleClimber.XYPoint p1 =
		 * points.get(s.p1index); SimpleClimber.XYPoint p2 = points.get(s.p2index); SimpleClimber.XYPoint
		 * dif1 = s.p1distance().scale(0.6); SimpleClimber.XYPoint dif2 =
		 * s.p1distance().scale(-0.3); p2.translate(dif1); p1.translate(dif2); }
		 * 
		 * // limit the angles
		 * 
		 * for (SimpleClimber.Joint j : joints) { SimpleClimber.XYPoint v1 = j.s1.vector(); SimpleClimber.XYPoint v2 =
		 * j.s2.vector();
		 * 
		 * double angle = v1.angle(v2);
		 * 
		 *
		 * if (angle < j.mina) { SimpleClimber.XYPoint v2r = v2.rotate(-(angle-j.mina));
		 * points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r)); }
		 * else if (angle > j.maxa) { SimpleClimber.XYPoint v2r = v2.rotate((j.maxa-angle));
		 * points.get(j.s2.p2index).set(points.get(j.s2.p1index).add(v2r)); } }
		 */

	}

	private boolean canmovelim(XYPoint bf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lsn = ls.add(bf);
		Circle lhc = new Circle(lh, lrad.length);
		Circle lsc = new Circle(lsn, lhum.length);
		XYPoint[] inter = lhc.intersect(lsc);
		return inter != null;
	}

	private void domovelim(XYPoint bf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lsn = ls.add(bf);
		Circle lhc = new Circle(lh, lrad.length);
		Circle lsc = new Circle(lsn, lhum.length);
		XYPoint[] inter = lhc.intersect(lsc);
		if (inter != null) {
			double d1 = inter[0].distance(le);
			double d2 = inter[1].distance(le);
			if (d1 < d2) {
				le.set(inter[0]);
			} else {
				le.set(inter[1]);
			}
		}
	}

	private void movelim(XYPoint lhf, XYPoint lh, XYPoint le, XYPoint ls,
			Spring lrad, Spring lhum) {
		XYPoint lhn = lh.add(lhf);
		Circle lhc = new Circle(lhn, lrad.length);
		Circle lsc = new Circle(ls, lhum.length);

		XYPoint[] inter = lhc.intersect(lsc);

		// still need to check angle constraints

		if (inter != null) {
			lh.set(lhn);
			double d1 = inter[0].distance(le);
			double d2 = inter[1].distance(le);
			if (d1 < d2) {
				le.set(inter[0]);
			} else {
				le.set(inter[1]);
			}
		}
		lhf.zero();
	}

	public void mouseDragged(MouseEvent arg0) {
	}

	public void mouseMoved(MouseEvent m) {
		// System.out.println("Mouse move");

		double scale = 0.03;

		Point p = m.getPoint();
		if (lastpoint != null) {

			double xm = scale * (p.getX() - lastpoint.getX());
			double ym = scale * -(p.getY() - lastpoint.getY());

			if (leftaction) {
				lhf.x = xm;
				lhf.y = ym;
			} else if (rightaction) {
				rhf.x = xm;
				rhf.y = ym;

			} else if (leftfaction) {
				lff.x = xm;
				lff.y = ym;

			} else if (rightfaction) {
				rff.x = xm;
				rff.y = ym;

			} else if (bodyaction) {
				bf.x = xm;
				bf.y = ym;
			}
		}
		lastpoint = p;
		//			stop swing after 5 seconds
	}

	public void keyPressed(KeyEvent k) {
		// System.out.println("Key pressed");
		int kc = k.getKeyCode();
		if (kc == KeyEvent.VK_A) {
			leftaction = true;
		} else if (kc == KeyEvent.VK_S) {
			rightaction = true;
		} else if (kc == KeyEvent.VK_Z) {
			leftfaction = true;
		} else if (kc == KeyEvent.VK_X) {
			rightfaction = true;
		} else if (kc == KeyEvent.VK_SPACE) {
			bodyaction = true;
			t.start();
		}
	}

	public void keyReleased(KeyEvent k) {
		// System.out.println("Key released");
		int kc = k.getKeyCode();
		if (kc == KeyEvent.VK_A) {
			leftaction = false;
		} else if (kc == KeyEvent.VK_S) {
			rightaction = false;
		} else if (kc == KeyEvent.VK_Z) {
			leftfaction = false;
		} else if (kc == KeyEvent.VK_X) {
			rightfaction = false;
		} else if (kc == KeyEvent.VK_SPACE) {
			bodyaction = false;
		}
	}

	public void keyTyped(KeyEvent arg0) {
	}

}
