package util;


import org.newdawn.slick.geom.Vector2f;


public class Utility {
	public static double getAngleDiffernce(double currentAngle, double targetAngle){
		return Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(targetAngle-currentAngle)), Math.cos(Math.toRadians(targetAngle-currentAngle))));
	}
	public static Vector2f getVectorFromArrayLocation(int xIndex, int yIndex, int size, Object[][] array){
		double xOutput = (size * (xIndex - (array.length / 2)))     + (array.length % 2 == 0 ? (size / 2f) : 0);
		double yOutput = (size * (yIndex - (array[0].length / 2)))  + (array[0].length % 2 == 0 ? (size / 2f) : 0);
		return new Vector2f((float)xOutput, (float)yOutput);
	}
	public static double distance(double x1, double y1, double x2, double y2){
		return Math.hypot(x1-x2, y1-y2);
	}
}
