package MyRobots;
import robocode.*;

public class Dummy extends TeamRobot implements Droid
{
	private int moveDirection = 1;
	private double enemyBearing = 0;
	private double firePower = 0;
	private boolean isLeaderAlive = true; 
	private int tooCloseToWall = 0;
	private int wallMargin = 60;
	private boolean goToWall = false;
	private String leaderName = null;
	public void run()
	{
		setAdjustGunForRobotTurn(true);
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
			if(isLeaderAlive)
			{
				doGun1();
				doTank1();
			}
			else
			{
				doGun2();
				doTank2();
			}
			execute();
		}
	}

	public void doGun1()
	{
		if(enemyBearing > 0)
		{
			setTurnGunRight(normalizeBearing(enemyBearing - getGunHeading()));
			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
				setFire(firePower);
		}
		else
			return;
	}

	public void doGun2()
	{
		if(getTime() % 50 == 0)
		{
			fire(2);
		}
	}
	public void doTank1()
	{
		setTurnRight(normalizeBearing(enemyBearing + 90 - (15 * moveDirection)));

	// if we're close to the wall, eventually, we'll move away
		if (tooCloseToWall > 0) tooCloseToWall--;

	// normal movement: switch directions if we've stopped
		if (getVelocity() == 0) {
			setMaxVelocity(8);
			moveDirection *= -1;
			setAhead(10000 * moveDirection);
		}
	}

	public void doTank2()
	{
		if(goToWall){
			double moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
			turnLeft(getHeading() % 90);
			ahead(moveAmount);
			turnRight(90);
			goToWall = false;
		}
		ahead(moveAmount);
		turnRight(90);
	}

	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if(isLeaderAlive)
		{
			tooCloseToWall = 0;
		}
		else
		{
			if (e.getBearing() > -90 && e.getBearing() < 90) {
			back(100);
			} // else he's in back of us, so set ahead a bit.
			else {
			ahead(100);
			}
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

	public void onMessageReceived(MessageEvent e)
	{
		leaderName = e.getSender();
		enemyBearing = e.getMessage().getabsDeg();
		firePower = e.getMessage().getFirePower();
	}

	public void onRobotDeath(RobotDeathEvent e)
	{
		if (e.getName().equals(leaderName))
			isLeaderAlive = false;
		else
			return;
	}
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
}

