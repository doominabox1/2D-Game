package ships;

import java.awt.Point;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import def.Renderable;
import pairobjects.ShipPartPoint;
import pairobjects.ThrusterCluster;
import util.MiniPID;
import util.ShipNotInitializedException;
import util.Utility;

public class AIShip  extends Ship implements Renderable{
	private boolean initialized = false;
	
	private ThrusterCluster forwardThrusters;	// Thrusters to move the ship forward
	private ThrusterCluster backwardThrusters;
	private ThrusterCluster clockwiseThrusters;
	private ThrusterCluster counterClockwiseThrusters;
	
	private Vector2f targetPosition;
	private Vector2f targetVelocity;
	private double targetRange;
	private double targetAngle;
	MiniPID anglePID;

	public AIShip(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException {
		super(xSize, ySize, spriteSheetPath, spriteSize);
		
		targetPosition = new Vector2f(0, 0);
		targetVelocity = new Vector2f(0, 0);
		targetRange = 250;
		targetAngle = angle;
		anglePID = new MiniPID(1, 0.1, 30); 
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
		
//		System.out.println(forwardThrusters); // Print for debug
//		System.out.println(backwardThrusters);
//		System.out.println(clockwiseThrusters);
//		System.out.println(counterClockwiseThrusters);
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
			
//			pts.add(new Vector2f[]{tPosition, tDirection});
			firingThrusters.add(new ShipPartPoint(ship[thruster.point.x][thruster.point.y], new Point(thruster.point.x, thruster.point.y)));
			applyForce(tPosition, tDirection);
		}
	}
	
	@Override
	public void update(int delta, Input input){
		
//		if(input.isKeyDown(Input.KEY_SPACE)){
//			position.set(500, 500);
//			velocity.set(0,0);
//		}

		Vector2f temp = new Vector2f(velocity);	// Update position using velocity and delta
		temp.scale(delta);
		position.add(temp);
		
		if(velocity.length() > 0.2){	// Clamp max speed to 0.2
			velocity.normalise();
			velocity.scale(0.2f);
		}
		
		angle += angularVelocity * delta;	// Update rotation
		
		if(angle > 360){					// Keep rotation between 0 and 360 
			angle -= 360;
		} else if(angle < 0 ){
			angle += 360;
		}
		
		
		// Ship AI
		float distanceToTarget = targetPosition.distanceSquared(position); 
		
		Vector2f alterableTargetPosition = new Vector2f(targetPosition);
		alterableTargetPosition.sub(position);
		
		// TODO Make target angle relative to how fast target is moving 
		
		if(distanceToTarget > targetRange * targetRange){	// If the target is close to being within range, slow down
			targetAngle = alterableTargetPosition.getTheta();
			Vector2f curVel = new Vector2f(velocity);
			curVel.normalise();
			curVel.scale(velocity.length());
			
			Vector2f tarVel = new Vector2f(alterableTargetPosition);
			tarVel.normalise();
			tarVel.scale(velocity.length() * 2);
			
			targetAngle = Math.toDegrees(Math.atan2(tarVel.y - curVel.y, tarVel.x - curVel.x)); 
		}else{
			targetAngle = velocity.getTheta();
		}
		
//		pts.clear();
		firingThrusters.clear();
		// TODO fix thrusters firing greater than their max thrust
		double angleDifference = Utility.getAngleDiffernce(angle, targetAngle);	// Use PID to set the ships rotation
        double anglePIDOutput = anglePID.getOutput(angle, angle + angleDifference);
        if(anglePIDOutput > 0){
            accelerateClockwise(Math.abs(anglePIDOutput));
        }else{
            accelerateCounterClockwise(Math.abs(anglePIDOutput));
        }

        if(distanceToTarget > targetRange * targetRange){
	        if(angleDifference < Math.abs(10)){
	    		accelerateForward(1);
	        }
        }else{
        	if(angleDifference < Math.abs(25)){
        		if(velocity.length() > 0.005){
        			accelerateBackward(1.5);
        		}
	        }
        }
		// End ship AI
        
        
		if(changed){	// Update center of mass and ship physics
			updateBlocks();
			hull.update();
			changed = false;
		}
		
	}
	
	@Override
	public void render(GameContainer gc, Graphics g, Rectangle camera) {
		if(positionOnScreen == null){
			positionOnScreen = new Vector2f(gc.getWidth() / 2, gc.getHeight() / 2);
		}
		if(!(camera.contains(getSimpleClippingHull()) || camera.intersects(getSimpleClippingHull()))){
			return;
		}
		Vector2f drawPosition = new Vector2f(position);
		drawPosition.sub(camera.getLocation());
		for (int i = 0; i < ship.length; i++) {			// Draw each ship component
			for (int j = 0; j < ship[0].length; j++) {
				Image curImage = hullList[i][j];
				if(curImage == null){
					continue;
				}
				Vector2f p = Utility.getVectorFromArrayLocation(i, j, shipSize, ship);
				curImage.setCenterOfRotation(center.x - p.x, center.y - p.y);
				curImage.setRotation((float) angle);
				curImage.draw(drawPosition.x - center.x + p.x, drawPosition.y - center.y + p.y, (float) shipSize, (float) shipSize);
			}
		}
		if(firingThrusters != null){	// Draw thruster points
			for (ShipPartPoint spp : firingThrusters) {
				Point thrusterLocation = spp.shipPart.getThrusterLocation(); 
				Image curImage = spriteSheet.getSubImage(5 + spp.shipPart.getDirection() / 90, 0);
				Vector2f p = Utility.getVectorFromArrayLocation(spp.point.x + thrusterLocation.x, spp.point.y + thrusterLocation.y, shipSize, ship);
				curImage.setCenterOfRotation(center.x - p.x, center.y - p.y);
				curImage.setRotation((float) angle);
				curImage.draw(drawPosition.x - center.x + p.x, drawPosition.y - center.y + p.y, (float) shipSize, (float) shipSize);
			}
		}
		if(debug){
//			g.setColor(Color.blue);
//			g.setLineWidth(3);
//			g.draw(getHull().transform(Transform.createRotateTransform((float) rAngle())).transform(Transform.createTranslateTransform(drawPosition.x, drawPosition.y)));
//			g.resetLineWidth();
			
//			g.setColor(Color.green);
//			if(pts != null){
//				for (Vector2f[] vs : pts) {
//					g.drawOval(drawPosition.x + vs[0].x, drawPosition.y + vs[0].y, 2, 2);
//					vs[1].scale(8000);
//					g.drawLine(drawPosition.x + vs[0].x, drawPosition.y + vs[0].y, drawPosition.x + vs[0].x + vs[1].x, drawPosition.y + vs[0].y + vs[1].y);
//				}
//			}
			
			g.setColor(Color.blue);
			Vector2f dbg = new Vector2f(targetAngle);
			dbg.scale(50);
			g.drawLine(100, 100, 100 + dbg.getX(), 100 + dbg.getY());
			
			g.setColor(Color.green);
			dbg = new Vector2f(velocity);
			dbg.normalise();
			dbg.scale(50);
			g.drawLine(100, 100, 100 + dbg.getX(), 100 + dbg.getY());
			
			g.setColor(Color.red);
			dbg = new Vector2f(angle);
			dbg.scale(50);
			g.drawLine(100, 100, 100 + dbg.getX(), 100 + dbg.getY());
			
			g.drawLine(100, 200, (float) (100 + angularVelocity * 100), 200);
			
			g.drawOval((float) (-camera.getX() + targetPosition.x - targetRange), (float) (-camera.getY() + targetPosition.y - targetRange), (float)targetRange * 2, (float)targetRange * 2);
		}
	}
	public void setTarget(Vector2f newTargetPosition, Vector2f newTargetVelocity) {
		targetPosition.set(newTargetPosition.copy());
		targetVelocity.set(newTargetVelocity.copy());
	}
	public static AIShip getRandomAIShip(int xSize, int ySize, String spriteSheetPath, int spriteSize) throws SlickException{
		AIShip newShip = new AIShip(xSize, ySize, spriteSheetPath, spriteSize);
		
		ShipPart sp;
		int turnThrusterLocation = Utility.randInt(1, ySize - 2, rand);
		for (int y = 0; y < ySize; y++) {	// TODO needs to be built sideways, or I need to fix AIShip to be based on north instead of east
			int layerSize = Utility.randInt(1, xSize / 2, rand);
			for(int x = (xSize / 2) - (layerSize - 1); x <= (xSize / 2) + (layerSize - 1); x++){
				sp = new ShipPart(ShipPart.TIER1, ShipPart.NOTHING, ShipPart.TIER1, ShipPart.NORTH);
				newShip.setPart(sp, x, y);
			}
			
			if(y != 0 && y != ySize - 1 && (rand.nextDouble() < 0.5 || y == turnThrusterLocation)){
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.EAST);
				newShip.setPart(sp, (xSize / 2) - (layerSize - 1), y);
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.WEST);
				newShip.setPart(sp, (xSize / 2) + (layerSize - 1), y);
			}
			
			if(y == 0){
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.SOUTH);
				newShip.setPart(sp, (xSize / 2) - (layerSize - 1), 0);
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.SOUTH);
				newShip.setPart(sp, (xSize / 2) + (layerSize - 1), 0);
			}
			if(y == ySize - 1){
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
				newShip.setPart(sp, (xSize / 2) - (layerSize - 1), ySize - 1);
				sp = new ShipPart(ShipPart.TIER2, ShipPart.THRUSTER, ShipPart.TIER1, ShipPart.NORTH);
				newShip.setPart(sp, (xSize / 2) + (layerSize - 1), ySize - 1);
			}
		}
		
		return newShip;
	}
}
