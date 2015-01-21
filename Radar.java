package MyRobots;

import robocode.*;

public class Radar implements RobotPart
{
	void init()
	{
		setAdjustRadarForGunTurn(true);
	}
	void move(EnemyBot enemy)
	{
		setTurnRadarRight(360);
	}
}