package def;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

import pairobjects.KeyWeight;

public class ShipPart {
	public static final byte NOTHING = -1;
	public static final byte TIER1 = 0;
	public static final byte TIER2 = 1;
	public static final byte TIER3 = 2;
	public static final byte TIER4 = 3;
	
	public static final byte FLAC		= 0;
	public static final byte LASER		= 1;
	public static final byte BULLET		= 2;
	public static final byte THRUSTER	= 3;
	public static final byte SHIELD		= 4;
	public static final byte COCKPIT	= 5;
	
	public static final short EAST  = 0;
	public static final short NORTH = 90;
	public static final short WEST  = 180;
	public static final short SOUTH = 270;
	
	private byte hullTier;
	private byte tool;
	private byte toolTier;
	private int partHealth;
	private short partDirection;
	private double mass;
	
	ArrayList<KeyWeight> mappedKeys = new ArrayList<KeyWeight>();
	
	public ShipPart(){
		hullTier = NOTHING;
		tool = NOTHING;
		toolTier = NOTHING;
		partHealth = getMaxHealth();
		partDirection = NOTHING;
		mass = NOTHING;
	}
	
	public ShipPart(byte inputHullTier, byte inputToolType, byte inputToolTier, short inputPartDirection){
		hullTier = inputHullTier;
		tool = inputToolType;
		toolTier = inputToolTier;
		partHealth = getMaxHealth();
		partDirection = inputPartDirection;
		mass = inputHullTier + 1;
	}
	
	public boolean addKeyMap(int key, double power){
		if(power < 0 || power > 1){
			throw new IllegalArgumentException("Power must be between 0 and 1, not " + power);
		}
		for(KeyWeight kp : mappedKeys){
			if(kp.getKey() == key){
				return false;
			}
		}
		mappedKeys.add(new KeyWeight(key, power));
		return true;
	}
	
	public void removeKeyMap(int key){
		Iterator<KeyWeight> iter = mappedKeys.iterator();
		while (iter.hasNext()) {
		    if (iter.next().getKey() == key) {
		        iter.remove();
		    }
		}
	}
	
	public Vector2f getThrust(float thrust, Input input){
		
		if(tool != ShipPart.THRUSTER){
			return null;
		}
		Vector2f out = new Vector2f(0, 0);
		Vector2f temp;
		double totalWeight = 0;
		for (KeyWeight kp : mappedKeys) {
			if(input.isKeyDown(kp.getKey())){
				temp = new Vector2f(partDirection);
				temp.scale((float)(thrust * kp.getWeight()));
				totalWeight += kp.getWeight();
				out.add(temp);
			}
		}
		totalWeight = Math.max(0, Math.min(1, totalWeight));
		return out;
	}
	
	public Vector2f getAbstractThrust(float thrust){
		Vector2f out = new Vector2f(partDirection);
		out.scale(thrust);
		return out;
	}
	
	public int getHealth(){
		return partHealth;
	}
	public void setHealth(int health){
		partHealth = health;
	}
	public int getMaxHealth(){
		return 25 * (hullTier + 1);
	}
	public void heal(){
		partHealth = getMaxHealth();
	}
	
	public byte getHullTier(){
		return hullTier;
	}
	public byte getTool(){
		return tool;
	}
	public byte getToolTier(){
		return toolTier;
	}
	
	public void setTool(byte newTool){
		if(newTool < -1 || newTool > 5){
			throw new IllegalArgumentException("Input must be -1 to 5, not " + newTool);
		}
		tool = newTool;
	}
	
	public void setToolTier(byte newToolTier){
		if(newToolTier < 0 || newToolTier > 3){
			throw new IllegalArgumentException("Input must be 0 to 3, not " + newToolTier);
		}
		toolTier = newToolTier;
	}
	
	public void setHullTier(byte newHullTier){
		if(newHullTier < 0 || newHullTier > 3){
			throw new IllegalArgumentException("Input must be 0 to 3, not " + newHullTier);
		}
		hullTier = newHullTier;
	}

	public double getMass() {
		return mass;
	}
}
