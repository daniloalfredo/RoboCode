package MyRobots;
import robocode.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Kamikaze - a robot by (your name here)
 */
public class Kamikaze extends AdvancedRobot
{
	/**
	 * run: Kamikaze's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		while(true) {
			// Replace the next 4 lines with any behavior you would like
			setTurnRadarRight(360);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
		setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
		double firePower = Math.min(700 / e.getDistance(), 3);
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
			setFire(firePower);
		execute();
		turnRight(e.getBearing());
		ahead(e.getDistance());
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		turnRight(e.getBearing() + 90);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
