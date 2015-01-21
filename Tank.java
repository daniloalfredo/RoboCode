package MyRobots;

import robocode.*;
import java.awt.*;

public class Tank implements RobotPart
{
	void init()
	{
		setColors(Color.Red, Color.Blue, Color.Black, null, null);
	}
	void move(EnemyBot enemy)
	{
		// always square off against our enemy, turning slightly toward him
	setTurnRight(enemy.getBearing() + 90 - (10 * moveDirection));

	// if we're close to the wall, eventually, we'll move away
	if (tooCloseToWall > 0) tooCloseToWall--;

	// normal movement: switch directions if we've stopped
	if (getVelocity() == 0) {
		moveDirection *= -1;
		setAhead(10000 * moveDirection);
	}
}