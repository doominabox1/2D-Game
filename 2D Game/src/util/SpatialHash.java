package util;

import interfaces.Renderable;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import ships.Ship;

public class SpatialHash {
	private int bucketSize;
	private ArrayList<Renderable> alwaysRender = new ArrayList<Renderable>();
	private HashMap<Point, ArrayList<Ship>> ships;
	private Comparator<Renderable> comparator = new RenderableComparator();
	private PriorityQueue<Renderable> renderObjects = new PriorityQueue<Renderable>(comparator);

	public SpatialHash(int bucketSize){
		this.bucketSize = bucketSize;
		ships = new HashMap<Point, ArrayList<Ship>>();
	}
	public void update(Ship ship, Vector2f movement){
		Shape clippingHull = ship.getSimpleClippingHull().transform(Transform.createTranslateTransform(-movement.x, -movement.y));
		Point topLeft = hash(clippingHull.getMinX(), clippingHull.getMinY());
		Point bottomRight = hash(clippingHull.getMaxX(), clippingHull.getMaxY());
		for(int x = topLeft.x; x <= bottomRight.x; x++){
			for(int y = topLeft.y; y <= bottomRight.y; y++){
				Point key = new Point(x, y);
				if(ships.containsKey(key)){
					ships.get(key).remove(ship);
				}
			}
		}
		add(ship);
	}
	public void remove(Ship ship){
		Rectangle clippingHull = ship.getSimpleClippingHull();
		Point topLeft = hash(clippingHull.getMinX(), clippingHull.getMinY());
		Point bottomRight = hash(clippingHull.getMaxX(), clippingHull.getMaxY());
		for(int x = topLeft.x; x <= bottomRight.x; x++){
			for(int y = topLeft.y; y <= bottomRight.y; y++){
				Point key = new Point(x, y);
				if(ships.containsKey(key)){
					ships.get(key).remove(ship);
				}
			}
		}
	}
	public void add(Ship ship){
		Rectangle clippingHull = ship.getSimpleClippingHull();
		Point topLeft = hash(clippingHull.getMinX(), clippingHull.getMinY());
		Point bottomRight = hash(clippingHull.getMaxX(), clippingHull.getMaxY());
		for(int x = topLeft.x; x <= bottomRight.x; x++){
			for(int y = topLeft.y; y <= bottomRight.y; y++){
				Point key = new Point(x, y);
				if(!ships.containsKey(key)){
					ships.put(key, new ArrayList<Ship>());
				}
				ships.get(key).add(ship);
			}
		}
	}
	public PriorityQueue<Renderable> getShipsToRender(Rectangle camera){
		Point topLeft = hash(camera.getMinX(), camera.getMinY());
		Point bottomRight = hash(camera.getMaxX(), camera.getMaxY());
		renderObjects.clear();
		for(int x = topLeft.x; x <= bottomRight.x; x++){
			for(int y = topLeft.y; y <= bottomRight.y; y++){
				Point key = new Point(x, y);
				if(ships.containsKey(key)){
					renderObjects.addAll(ships.get(key));
				}
			}
		}
		renderObjects.addAll(alwaysRender);
		return renderObjects;
	}
	private Point hash(Ship ship){
		return new Point((int) (ship.getX() / bucketSize), (int) (ship.getY() / bucketSize));
	}
	private Point hash(double x, double y){
		return new Point((int) (x / (double)bucketSize), (int) (y / (double)bucketSize));
	}
	public Ship lineHitDetect(Ship source, Line line){  // Use lineDetect to get ships to scan, then find the closest ship that it hits
		// TODO fix
		// return destination.getSimpleClippingHull().intersects(line) && destination.getClippingHull().intersects(line);
		return null;
	}
	public ArrayList<Point> getShipsNearLine(Ship ship, Line line){ // Gets a list of all ships that intersect `line` besides `ship` 
	    // TODO https://www.gamedev.net/resources/_/technical/game-programming/spatial-hashing-r2697
	    double x0 = (line.getX1() / bucketSize);
	    double y0 = (line.getY1() / bucketSize);
	    double x1 = (line.getX2() / bucketSize);
	    double y1 = (line.getY2() / bucketSize);
	    boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
	    ArrayList<Point> lst = new ArrayList<Point>();

	    if (steep){
	        lst.addAll( getShipsNearLine(ship, new Line( (int)(y0 * bucketSize), (int)(x0 * bucketSize), (int)(y1 * bucketSize), (int)(x1 * bucketSize))));
	    }
	    if (x0 > x1){
	        lst.addAll( getShipsNearLine(ship, new Line( (int)(x1 * bucketSize), (int)(y1 * bucketSize), (int)(x0 * bucketSize), (int)(y0 * bucketSize))));
	    }

	    double dx = x1 - x0;
	    double dy = y1 - y0;
	    double gradient = dy / dx;

	    // handle first endpoint
	    double xend = Math.round(x0);
	    double yend = y0 + gradient * (xend - x0);
	    double xpxl1 = xend; // this will be used in the main loop
	    double ypxl1 = ipart(yend);

	    if (steep) {
	        lst.add(new Point((int)ypxl1, (int)xpxl1));
	        lst.add(new Point((int)ypxl1 + 1, (int)xpxl1));
	    } else {
	        lst.add(new Point((int)xpxl1, (int)ypxl1));
	        lst.add(new Point((int)xpxl1, (int)ypxl1 + 1));
	    }

	    // first y-intersection for the main loop
	    double intery = yend + gradient;

	    // handle second endpoint
	    xend = Math.round(x1);
	    yend = y1 + gradient * (xend - x1);
	    double xpxl2 = xend; // this will be used in the main loop
	    double ypxl2 = ipart(yend);

	    if (steep) {
	        lst.add(new Point((int)ypxl2, (int)xpxl2));
	        lst.add(new Point((int)ypxl2 + 1, (int)xpxl2));
	    } else {
	        lst.add(new Point((int)xpxl2, (int)ypxl2));
	        lst.add(new Point((int)xpxl2, (int)ypxl2 + 1));
	    }

	    // main loop
	    for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
//	    	lst.add(new Point((int)x, (int)intery));
//            lst.add(new Point((int)x, (int)intery + 1));
	    	if (steep) {
	            lst.add(new Point((int)intery, (int)x));
	            lst.add(new Point((int)intery + 1, (int)x));
	        } else {
	            lst.add(new Point((int)x, (int)intery));
	            lst.add(new Point((int)x, (int)intery + 1));
	        }
	        intery = intery + gradient;
	    }
	    return lst;
	}
	double fpart(double x) {
	    return x - Math.floor(x);
	}
	double rfpart(double x) {
	    return 1.0 - fpart(x);
	}
	int ipart(double x) {
	    return (int) x;
	}

	public ArrayList<Renderable> getAlwaysRenderList(){
		return alwaysRender;
	}
}

