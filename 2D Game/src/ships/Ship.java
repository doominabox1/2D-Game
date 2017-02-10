package ships;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import pairobjects.ShipPartPoint;
import util.IllegalBlockLocationException;
import util.SpatialHash;
import util.Utility;


public abstract class Ship {
	protected Vector2f position; 
	protected Vector2f velocity;
	protected Vector2f center;
	protected Vector2f positionOnScreen;

	protected double angle;
	protected double angularVelocity;
	protected int shipSize = 20;
	protected double shipMass;
	protected final float thrustMultiplier = 0.05f;
	
	protected SpriteSheet spriteSheet;
	
	protected Image[][] hullList;
	protected ShipPart[][] ship;
	protected boolean changed = true;
	protected boolean debug = true;
	protected ArrayList<ShipPartPoint> firingThrusters = new ArrayList<ShipPartPoint>();
	
	protected Hull hull;
	protected int priority;
	protected static Random rand = new Random();
	
	public Ship(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException{
		ShipPart[][] shp = new ShipPart[xSize][ySize];
		for (int i = 0; i < shp.length; i++) {
			for (int j = 0; j < shp[0].length; j++) {
				shp[i][j] = new ShipPart();
			}
		}
		ship = shp;
		spriteSheet = new SpriteSheet(spriteSheetPath, spriteSize, spriteSize);
		position = new Vector2f(0, 0); 
		velocity = new Vector2f(0, 0);
		center = new Vector2f(0, 0);
		priority = 0;
		updateBlocks();
	}

	protected void updateBlocks(){
		double topX = 0;
		double topY = 0;
		double bottom = 0;
		hullList = new Image[ship.length][ship[0].length];
		for (int i = 0; i < ship.length; i++) {
			for (int j = 0; j < ship[0].length; j++) {
				if(ship[i][j].getHullTier() == ShipPart.NOTHING){
					continue;
				}
				Vector2f p = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				topX += ship[i][j].getMass() * p.x;
				topY += ship[i][j].getMass() * p.y;
				bottom += ship[i][j].getMass();
				
				hullList[i][j] = spriteSheet.getSubImage(ship[i][j].getHullTier(), 0);
				if(ship[i][j].getTool() == ShipPart.THRUSTER){
					Point thrusterLocation = ship[i][j].getThrusterLocation(); 
					try{
						if(ship[i + thrusterLocation.x][j + thrusterLocation.y].getHullTier() != ShipPart.NOTHING){
							throw new IllegalBlockLocationException("Block obstructiong thruster: " + (i + thrusterLocation.x) + " " + (j + thrusterLocation.y));
						}
					}catch(ArrayIndexOutOfBoundsException e){
						continue;
					}
				}
			}
		}
		center = new Vector2f((float)((topX / bottom) + (shipSize / 2)), (float)((topY / bottom) + (shipSize / 2)));
		shipMass = bottom;
	}
	
	public void update(int delta, Input input, SpatialHash sh){
		sh.remove(this);
		sh.add(this);
	}
	
	public void render(GameContainer gc, Graphics g) {

	}
	
	public Polygon getHull(){
		if(hull == null){
			hull = new Hull(ship, this);
		}
		return hull.getHull();
	}
	
	public Polygon getClippingHull(){
		if(hull == null){
			hull = new Hull(ship, this);
		}
		return (Polygon) hull.getHull().transform(Transform.createRotateTransform((float) rAngle())).transform(Transform.createTranslateTransform(position.x, position.y));
	}
	public Rectangle getSimpleClippingHull(){
		Polygon hull = getClippingHull();
		return new Rectangle(hull.getMinX(), hull.getMinY(), hull.getWidth(), hull.getHeight());
	}
	
	
	protected Vector2f getRotatedPoint(Vector2f v, double angle){
		Vector2f temp = new Vector2f(v);
		Vector2f temp2 = new Vector2f(angle + 45);
		temp2.scale((float)(Math.sqrt(2) * shipSize * 0.5));
		temp.sub(center);
		temp.add(angle);
		temp.add(temp2);
		return temp;
	}
	
	protected void applyForce(Vector2f position, Vector2f force){ 
		if (force.length() == 0) {
			return;
		}
		Vector2f forceAndMass = new Vector2f(force);
		forceAndMass.scale((float) (1 / shipMass));
		velocity.add(forceAndMass);
	    if(position.lengthSquared() < 1){
	    	return;
	    }
	    // the difference between the direction of force and direction to center
	    double pheta = Math.toRadians(position.getTheta() - force.getTheta());
	    
	    // The amount of the force that
	 	// contributes to angular acceleration
	 	// along the tangent from the center
	    double Fr = Math.sin(pheta) * force.length() * (position.length() * 10);
	    

	    // reduce angular acceleration by distance and mass
	    angularVelocity += -Fr / (shipMass * shipMass * shipMass);

	}
	
	protected double rAngle(){
		return angle * (Math.PI / (double)180);
	}
	public Vector2f getPosition(){
		return new Vector2f(position);
	}
	public Vector2f getPositionObject(){
		return position;
	}
	
	public double getX(){
		return position.x;
	}
	public double getY(){
		return position.y;
	}
	public void setPosition(float xPosition, float yPosition){
		position.set(new Vector2f(xPosition, yPosition));
	}
	public void setPosition(Vector2f p){
		position = new Vector2f(p); 
	}
	public void setPart(ShipPart part, int xPosition, int yPosition){
		ship[xPosition][yPosition] = part;
		changed = true;
	}
	public void setVelocity(Vector2f v) {
		velocity = new Vector2f(v);
	}
	public void restoreHealth(){
		for(ShipPart[] partArray : ship){
			for(ShipPart part : partArray){
				part.heal();
			}
		}
	}
	public int getSize() {
		return shipSize;
	}
	public Vector2f getCenterObject() {
		return center;
	}
	public Vector2f getVelocity() {
		return new Vector2f(velocity);
	}
	public int getRenderPriority() {
		return priority;
	}
	public void setRenderPriority(int newPriority) {
		priority = newPriority;
	}
	public double getAngle(){
		return angle;
	}
}
