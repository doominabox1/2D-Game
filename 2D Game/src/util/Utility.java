package util;


import java.util.Random;

import org.newdawn.slick.geom.Vector2f;


public class Utility {	// Some utility / math functions 
	public static double getAngleDiffernce(double currentAngle, double targetAngle){ // Gets the angle between two angles
		return Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(targetAngle-currentAngle)), Math.cos(Math.toRadians(targetAngle-currentAngle))));
	}
	public static Vector2f getVectorFromArrayLocation(int xIndex, int yIndex, int size, Object[][] array){ // Physical location of a block in relation to an array index
		double xOutput = (size * (xIndex - (array.length / 2)))     + (array.length % 2 == 0 ? (size / 2f) : 0);
		double yOutput = (size * (yIndex - (array[0].length / 2)))  + (array[0].length % 2 == 0 ? (size / 2f) : 0);
		return new Vector2f((float)xOutput, (float)yOutput);
	}
	public static double distance(double x1, double y1, double x2, double y2){ // Distance formula 
		return Math.hypot(x1-x2, y1-y2);
	}
	public static int randInt(int min, int max, Random rand) {	// Random int between min and max, inclusive
	    return rand.nextInt((max - min) + 1) + min;
	}
}
