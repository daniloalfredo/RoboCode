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
		addCustomEvent(new Condition("too_close_to_walls") {
			public boolean test() {
				return (
						// we're too close to the left wall
						(getX() <= wallMargin ||
						 // or we're too close to the right wall
						 getX() >= getBattleFieldWidth() - wallMargin ||
						 // or we're too close to the bottom wall
						 getY() <= wallMargin ||
						 // or we're too close to the top wall
						 getY() >= getBattleFieldHeight() - wallMargin)
						);
					}
				});
		while(true)
		{
			execute();
		}
	}
	public void tank()
	{
		
	}  
	public void radar()
	{
		setTurnRadarRight(360);
	}

	public void onScannedRobot(ScannedRobotEvent e)
	{
		if(enemy.none() || e.getDistance() < enemy.getDistance() - 70 || e.getName().equals(enemy.getName()))
		{
			enemy.udpate(e, this);
		}
	}

	public void onRobotDeath(RobotDeathEvent e)
	{
		if(e.getName().equals(enemy.getName()))
		{
			enemy.reset();
		}
	}

	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("too_close_to_walls"))
		{
			if (tooCloseToWall <= 0) {
				// if we weren't already dealing with the walls, we are now
				tooCloseToWall += wallMargin;
				setMaxVelocity(0); // stop!!!
			}
		}
	}
	public void onHitRobot(HitRobotEvent e) { tooCloseToWall = 0; }

	public double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}

	// normalizes a bearing to between +180 and -180
	public double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}
	public void tiroFatal() {
	double tiro = (enemy.getEnergy() / 4) + .1;
	fire(tiro);
	}

//if(e.getEnergy < 12) {
//	tiroFatal(e.getEnergy);
//} else {
//	fire(2);
} 
} 