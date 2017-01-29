package ships;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

import def.Renderable;
import def.ShipPart;
import def.Utility;
import pairobjects.ShipPartPoint;
import pairobjects.ThrusterCluster;
import util.MiniPID;

public class AIShip  extends Ship implements Renderable{
	private boolean initialized = false;
	
	private ThrusterCluster forwardThrusters;
	private ThrusterCluster backwardThrusters;
	private ThrusterCluster clockwiseThrusters;
	private ThrusterCluster counterClockwiseThrusters;
	
	private Vector2f targetPosition;
	private double targetRange;
	private double targetAngle;
	MiniPID anglePID;
	
	
	public AIShip(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException {
		super(xSize, ySize, spriteSheetPath, spriteSize);
		
		targetPosition = new Vector2f(0, 0);
		targetRange = 100;
		targetAngle = angle;
		
		anglePID = new MiniPID(1, 0.001, 30); 
        anglePID.setOutputLimits(-1, 1);
	}
	
	public void initFunctions(){	// Initializes ship thruster functions by testing all possible combinations of thrusters up to 3
		updateBlocks();
		initialized = true;
		
		ArrayList<ShipPartPoint> thrusters = new ArrayList<ShipPartPoint>();
		for (int x = 0; x < ship.length; x++) {
			for (int y = 0; y < ship[0].length; y++) {
				if(ship[x][y].getTool() == ShipPart.THRUSTER){
					thrusters.add(new ShipPartPoint(ship[x][y], new Point(x, y)));
				}
			}
		}
		
		ArrayList<ShipPartPoint[]> subsets = new ArrayList<>();
		
		for(int k = 1; k <= Math.min(3, thrusters.size()); k++){ // k = sequence length
			int[] s = new int[k];                  // here we'll keep indices 
			                                       // pointing to elements in input array
		
			if (k <= thrusters.size()) {
			    // first index sequence: 0, 1, 2, ...
			    for (int i = 0; (s[i] = i) < k - 1; i++);  
			    subsets.add(getSubset(thrusters, s));
			    for(;;) {
			        int i;
			        // find position of item that can be incremented
			        for (i = k - 1; i >= 0 && s[i] == thrusters.size() - k + i; i--); 
			        if (i < 0) {
			            break;
			        } else {
			            s[i]++;                    // increment this item
			            for (++i; i < k; i++) {    // fill up remaining items
			                s[i] = s[i - 1] + 1; 
			            }
			            subsets.add(getSubset(thrusters, s));
			        }
			    }
			}
		}
		for (ShipPartPoint[] subset : subsets) {
			velocity.set(0, 0);
			angularVelocity = 0;
			for (ShipPartPoint part : subset) {
				Vector2f tDirection = part.shipPart.getAbstractThrust(thrustMultiplier);
				tDirection.add(angle);
				Vector2f tPosition = Utility.getVectorFromArrayLocation(part.point.x, part.point.y, shipSize, ship);
				tPosition = getRotatedPoint(tPosition, angle);
				applyForce(tPosition, tDirection);
			}
			if(forwardThrusters == null || velocity.x > forwardThrusters.vector.x){
				forwardThrusters = new ThrusterCluster(subset, angularVelocity, velocity);
			}
			if(backwardThrusters == null || velocity.x < backwardThrusters.vector.x){
				backwardThrusters = new ThrusterCluster(subset, angularVelocity, velocity);
			}
			if(clockwiseThrusters == null || (angularVelocity) - velocity.length() * 1 >= (clockwiseThrusters.angularVelocity) - clockwiseThrusters.vector.length() * 1){
				clockwiseThrusters = new ThrusterCluster(subset, angularVelocity, velocity);
			}
			if(counterClockwiseThrusters == null || (angularVelocity) + velocity.length() * 1 <= (counterClockwiseThrusters.angularVelocity) + counterClockwiseThrusters.vector.length() * 1){
				counterClockwiseThrusters = new ThrusterCluster(subset, angularVelocity, velocity);
			}
		}
		
		System.out.println(forwardThrusters);
		System.out.println(backwardThrusters);
		System.out.println(clockwiseThrusters);
		System.out.println(counterClockwiseThrusters);
		velocity.set(0, 0);
		angularVelocity = 0;
	}
	
	// generate actual subset by index sequence
	private ShipPartPoint[] getSubset(ArrayList<ShipPartPoint> input, int[] subset) {
		ShipPartPoint[] result = new ShipPartPoint[subset.length]; 
	    for (int i = 0; i < subset.length; i++) 
	        result[i] = input.get(subset[i]);
	    return result;
	}
	
	private void accelerateClockwise(double scale){
		if(!initialized){
			throw new ShipNotInitializedException("");
		}
		activateThrusters(clockwiseThrusters, scale);
	}
	private void accelerateCounterClockwise(double scale){
		if(!initialized){
			throw new ShipNotInitializedException("");
		}
		activateThrusters(counterClockwiseThrusters, scale);
	}
	private void accelerateForward(double scale){
		if(!initialized){
			throw new ShipNotInitializedException("");
		}
		activateThrusters(forwardThrusters, scale);
	}
	private void accelerateBackward(double scale){
		if(!initialized){
			throw new ShipNotInitializedException("");
		}
		activateThrusters(backwardThrusters, scale);
	}
	
	private void activateThrusters(ThrusterCluster tc, double scale){
		for (ShipPartPoint thruster : tc.thrusters) {
			Vector2f tDirection = thruster.shipPart.getAbstractThrust((float) (thrustMultiplier * scale));

			tDirection.add(angle);
			
			Vector2f tPosition = Utility.getVectorFromArrayLocation(thruster.point.x, thruster.point.y, shipSize, ship);
			tPosition = getRotatedPoint(tPosition, angle);
			
			if(tDirection.length() == 0){
				continue;
			}
			
			pts.add(new Vector2f[]{tPosition, tDirection});
			applyForce(tPosition, tDirection);
		}
	}
	
	@Override
	public void update(int delta, Input input){

		Vector2f temp = new Vector2f(velocity);	// Update position using velocity and delta
		temp.scale(delta);
		position.add(temp);
		
		angle += angularVelocity * delta;	// Update rotation
		
		if(angle > 360){					// Keep rotation between 0 and 360 
			angle -= 360;
		} else if(angle < 0 ){
			angle += 360;
		}
		
		if(angularVelocity > 10){			// Clamp rotation speed
			angularVelocity = 10;
		}else if(angularVelocity < -10){
			angularVelocity = -10;
		}
		
		pts.clear();
		
		// TODO Update target angle to make velocity vector point at target 
		
		double angleDifference = Utility.getAngleDiffernce(angle, targetAngle);		// Use PID to set the ships rotation
        double anglePIDOutput = anglePID.getOutput(angle, angle + angleDifference);
        if(anglePIDOutput > 0){
            accelerateClockwise(Math.abs(anglePIDOutput));
        }else{
            accelerateCounterClockwise(Math.abs(anglePIDOutput));
        }
		
        // TODO Update 
		
		if(changed){		// Update center of mass and ship physics
			updateBlocks();
			hull.update();
			changed = false;
		}
		
	}
	
	@Override
	public void render(GameContainer gc, Graphics g) {
		if(positionOnScreen == null){
			positionOnScreen = new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2);
		}
		for (int i = 0; i < ship.length; i++) {			// Draw each ship component
			for (int j = 0; j < ship[0].length; j++) {
				Image curImage = hullList[i][j];
				if(curImage == null){
					continue;
				}
				Vector2f p = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				curImage.setCenterOfRotation(center.x - p.x, center.y - p.y);
				curImage.setRotation((float) angle);
				curImage.draw(position.x - center.x + p.x, position.y - center.y + p.y, (float) shipSize, (float) shipSize);
			}
		}
		if(debug){
			g.setColor(Color.blue);
			g.setLineWidth(3);
			g.draw(getHull().transform(Transform.createRotateTransform((float) rAngle())).transform(Transform.createTranslateTransform(position.x, position.y)));
			g.resetLineWidth();
			
			g.setColor(Color.green);
			if(pts != null){
				for (Vector2f[] vs : pts) {
					g.drawOval(position.x + vs[0].x, position.y + vs[0].y, 2, 2);
					vs[1].scale(8000);
					g.drawLine(position.x + vs[0].x, position.y + vs[0].y, position.x + vs[0].x + vs[1].x, position.y + vs[0].y + vs[1].y);
				}
			}
			
			g.setColor(Color.red);
			Vector2f dbg = new Vector2f(angle);
			dbg.scale(50);
			g.drawLine(100, 100, 100 + dbg.getX(), 100 + dbg.getY());
			
			g.setColor(Color.blue);
			dbg = new Vector2f(targetAngle);
			dbg.scale(50);
			g.drawLine(100, 100, 100 + dbg.getX(), 100 + dbg.getY());
			
			g.drawLine(100, 200, (float) (100 + angularVelocity * 100), 200);
		}
	}
}
