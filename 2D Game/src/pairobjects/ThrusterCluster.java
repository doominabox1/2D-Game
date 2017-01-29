package pairobjects;

import org.newdawn.slick.geom.Vector2f;

public class ThrusterCluster {
	public ShipPartPoint[] thrusters;
	public double angularVelocity;
	public Vector2f vector;
	
	public ThrusterCluster(ShipPartPoint[] thrusters, double angularVelocity, Vector2f vector){
		this.thrusters = thrusters;
		this.angularVelocity = angularVelocity;
		this.vector = new Vector2f(vector);
	}
	
	public String toString(){
		return "Thrusters: " + thrusters.length + ", " + vector + ", Angular velocity: " + angularVelocity;
	}
}
