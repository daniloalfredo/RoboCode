package MyRobots;
import robocode.*;
import java.awt.geom.Point2D;
import java.util.StringTokenizer;
import java.util.Locale;
import java.awt.Color;

public class Dummy extends TeamRobot implements Droid
{
	private int moveDirection = 1;
	private double enemyVelocity = 0;
	private double enemyHeading = 0;
	private double enemyBearing = 0;
	private double enemyDistance = 0;
	private double enemyX = 0;
	private double enemyY = 0;
	private double firePower = 0;
	private boolean isLeaderAlive = true; 
	private int tooCloseToWall = 0;
	private int wallMargin = 60;
	private boolean goToWall = false;
	private String leaderName = null;

	private boolean lockon = false;
	public void run()
	{
		setAllColors(Color.BLACK);
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
			System.out.println("EnemyDistance:" + enemyDistance + "\nEnemyBearing: " + enemyBearing + "\nFirePower: " + firePower+"\n");
			if(!isLeaderAlive || getTeammates() == null)
			{
				//doGun2();
				doTank2();
			}
			else
			{
				doGun1();
				doTank1();
			}
			execute();
		}
	}

	public void doGun1()
	{
		enemyDistance = Point2D.distance(getX(), getY(), enemyX, enemyY);
		Pair par = calcTiro(enemyDistance, getX(), getY());
		enemyBearing = par.getabsDeg();
		setTurnRight(normalizeBearing(enemyBearing));
		firePower = par.getFirePower();
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
		//setTurnGunRight(enemyBearing);
		setFire(firePower);
	}
	public void doTank1()
	{
		/*if(lockon){
			setTurnRight(enemyBearing);
			lockon = false;
		}*/
		
		if (enemyDistance > 200)
			setAhead(enemyDistance / 2);
		// but not too close
		if (enemyDistance < 100)
			setBack(enemyDistance);

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
		setTurnRight(90);
		setAhead(100);
		if (getTime() % 20 == 0) {
			moveDirection *= -1;
			setAhead(150 * moveDirection);
		}
		/*double moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		if(goToWall){
			turnLeft(getHeading() % 90);
			ahead(moveAmount);
			turnRight(90);
			turnGunRight(90 - getGunHeading());
			goToWall = false;
		}
		ahead(moveAmount);
		turnRight(90);
		turnGunRight(90);*/
	}

	public void onHitRobot(HitRobotEvent e) {
		// If he's in front of us, set back up a bit.
		if(isLeaderAlive)
		{
			tooCloseToWall = 0;
		}
		else
		{
			setTurnGunRight(normalizeBearing(e.getBearing() - getGunHeading()));
			setFire(3);
		}
	}
	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("too_close_to_walls"))
		{
			if (tooCloseToWall <= 0 && !(!isLeaderAlive || getTeammates() == null)) {
				// if we weren't already dealing with the walls, we are now
				tooCloseToWall += wallMargin;
				setMaxVelocity(0); // stop!!!
			}
		}
	}

	public void onMessageReceived(MessageEvent e)
	{
		leaderName = e.getSender();
		String msg = (String)e.getMessage();
		//System.out.println(msg);
		StringTokenizer st = new StringTokenizer(msg, "@");
		enemyX = Double.parseDouble(st.nextToken());
		enemyY = Double.parseDouble(st.nextToken());
		enemyHeading = Double.parseDouble(st.nextToken());
		enemyVelocity = Double.parseDouble(st.nextToken());
		/*enemyBearing = Double.parseDouble(st.nextToken());
		firePower = Double.parseDouble(st.nextToken());
		enemyDistance = Double.parseDouble(st.nextToken());*/
	}

	public void onRobotDeath(RobotDeathEvent e)
	{
		if (e.getName().equals(leaderName)){
			isLeaderAlive = false;
			goToWall = true;
		}
		else
			return;
	}

	public void onHitByBullet (HitByBulletEvent e)
	{
		if(!isLeaderAlive)
		{
			setTurnRight(e.getBearing());
			setAhead(100);
			setTurnGunRight(normalizeBearing(e.getBearing() - getGunHeading()));
			setFire(e.getPower());
		}
	}

	public Pair calcTiro (double dist, double x, double y)
	{
		Pair par = new Pair();
		// calculate firepower based on distance
		double firePower = Math.min(500 / dist, 3);
		par.setFirePower(firePower);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long time = (long)(dist / bulletSpeed);

		// calculate gun turn to predicted x,y location
		double futureX = enemyX + Math.sin(Math.toRadians(enemyHeading)) * enemyVelocity * time;
		double futureY = enemyY + Math.cos(Math.toRadians(enemyHeading)) * enemyVelocity * time;
		par.setabsDeg(absoluteBearing(x, y, futureX, futureY));
		return par;
		// non-predictive firing can be done like this:
		//double absDeg = absoluteBearing(getX(), getY(), enemy.getX(), enemy.getY());
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
}

