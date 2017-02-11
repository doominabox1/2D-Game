package util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import ships.Ship;

public class SpatialHash {
	private int bucketSize;
	HashMap<Point, ArrayList<Ship>> ships;
	
	public SpatialHash(int bucketSize){
		this.bucketSize = bucketSize;
		ships = new HashMap<Point, ArrayList<Ship>>();
	}
	public void update(Ship ship, Vector2f movement){
		if(ship.getVelocity().lengthSquared() == 0){
			return;
		}
		
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
	public ArrayList<Ship> getShipsToRender(Rectangle camera){
		Point topLeft = hash(camera.getMinX(), camera.getMinY());
		Point bottomRight = hash(camera.getMaxX(), camera.getMaxY());
		ArrayList<Ship> renderObjects = new ArrayList<Ship>();
		for(int x = topLeft.x; x <= bottomRight.x; x++){
			for(int y = topLeft.y; y <= bottomRight.y; y++){
				Point key = new Point(x, y);
				if(ships.containsKey(key)){
					renderObjects.addAll(ships.get(key));
				}
			}
		}
		return renderObjects;
	}
	private Point hash(Ship ship){
		return new Point((int) (ship.getX() / bucketSize), (int) (ship.getY() / bucketSize));
	}
	private Point hash(double x, double y){
		return new Point((int) (x / (double)bucketSize), (int) (y / (double)bucketSize));
	}
	public static boolean lineHitDetect(Ship source, Line line){  // Use lineDetect to get ships to scan, then find the closest ship that it hits
		// TODO fix
		// return destination.getSimpleClippingHull().intersects(line) && destination.getClippingHull().intersects(line);
		return false;
	}
	private ArrayList<Ship> getShipsNearLine(Ship ship, Line line){ // Gets a list of all ships that intersect `line` besides `ship` 
		// TODO https://www.gamedev.net/resources/_/technical/game-programming/spatial-hashing-r2697
		return null;
	}
}
