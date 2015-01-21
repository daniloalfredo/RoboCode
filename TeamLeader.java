package MyRobots;
import robocode.*;

public class TeamLeader extends TeamRobot{
	private int radarDirection = 1;
	private int moveDirection = 1;
	private EnemyBot enemy = new EnemyBot();
	public void run()
	{
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		enemy.reset();
		movement();
		while(true)
		{
			setTurnRadarRight(360);
			execute();
		}
	}
	public void movement()
	{
		double movimento = getX();
		if(movimento > getBattleFieldWidth()/2){
			movimento = getBattleFieldWidth() - movimento;
			turnRight(90 - getHeading());
		}
		else
			turnRight(270 - getHeading());
		ahead(movimento - 1);
	}  
	public void girar()
	{

	}

	public void onHitWall (HitWallEvent e)
	{
		
	}
} 