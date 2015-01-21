package MyRobots;
import robocode.*;

public class Dummy extends TeamRobot implements Droid
{
	private int moveDirection = 1;
	private EnemyBot enemy = new EnemyBot();
	public void run()
	{
		setAdjustGunForRobotTurn(true);
		enemy.reset();
		movement();
		while(true)
		{
			execute();
		}
	}

	public void movement()
	{
		double movimento = getY();
		if(movimento > getBattleFieldHeight()/2){
			movimento = getBattleFieldHeight() - movimento;
			turnRight(-getHeading());
		}
		else
			turnRight(180 - getHeading());
		ahead(movimento - 1);
	}
}