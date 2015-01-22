package MyRobots;
import robocode.*;
import java.awt.geom.Point2D;

public class TeamLeader extends TeamRobot{
	private int moveDirection = 1;
	private EnemyBot enemy = new EnemyBot();
	private Pair par = new Pair();
	private EnemyBot[] droids = new EnemyBot[4];
	private droidCount = 4;
	private int tooCloseToWall = 0;
	private int wallMargin = 60;
	
	public void run()
	{
		String[] teammates = getTeammates();
		for (int i = 0; i < teammates.length; i++)
		{
			droids[i].setName(teammates[i]);
		}
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
			doRadar();
			if(droidCount > 0)
			{
				doGun();
				doTank();
			}
			else
			{
				doGun2();
				doTank2();
			}
			execute();
		}
	}
	//calcula o firePower, o ângulo de tiro e o bearing do inimigo
	//parâmetros: distância do inimigo, e posição do robô
	public void calcTiro (double dist, double x, double y)
	{
		// calculate firepower based on distance
		double firePower = Math.min(500 / enemy.getDistance(), 3);
		par.setFirePower(firePower);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long time = (long)(enemy.getDistance() / bulletSpeed);

		// calculate gun turn to predicted x,y location
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		par.setabsDeg(absoluteBearing(x, y, futureX, futureY));
		// non-predictive firing can be done like this:
		//double absDeg = absoluteBearing(getX(), getY(), enemy.getX(), enemy.getY());
	}

	public void doGun()
	{
		if (enemy.none())
				return;
		String message = NULL;
		for( int i = 0; i<4 ; i++)
		{
			calcTiro(Point2D.distance(droids[i].getx(), droids[i].gety(), enemy.getx(), enemy.gety()), droids[i].getx, droids[i].gety);
			sendMessage(droids[i].getName(), par);
		}
		// turn the gun to the predicted x,y location
		calcTiro(enemy.getDistance(), getX(), getY());
		setTurnGunRight(normalizeBearing(par.getabsDeg() - getGunHeading()));

		// if the gun is cool and we're pointed in the right direction, shoot!
		if (enemy.getDistance() < 300 && enemy.getEnergy() < 12)
		{
			tiroFatal();
		}
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10 && enemy.getDistance() < 300) {
			setFire(firePower);
		}
	}
	public void doGun2()
	{
		if (enemy.none())
				return;
		calcTiro(enemy.getDistance(), getX(), getY());
		setTurnGunRight(normalizeBearing(par.getabsDeg() - getGunHeading()));

		// if the gun is cool and we're pointed in the right direction, shoot!
		if (enemy.getDistance() < 300 && enemy.getEnergy() < 12)
		{
			tiroFatal();
		}
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	}
	public void doTank()
	{
		setTurnRight(enemy.getBearing() + 90);

	// strafe by changing direction every 20 ticks
		if (getTime() % 20 == 0) {
			moveDirection *= -1;
			setAhead(150 * moveDirection);
		}

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
		setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (15 * moveDirection)));

	// if we're close to the wall, eventually, we'll move away
		if (tooCloseToWall > 0) tooCloseToWall--;

	// normal movement: switch directions if we've stopped
		if (getVelocity() == 0) {
			setMaxVelocity(8);
			moveDirection *= -1;
			setAhead(10000 * moveDirection);
		}
	}
	public void doRadar()
	{
		setTurnRadarRight(360);
	}

	public void onScannedRobot(ScannedRobotEvent e)
	{
		if((enemy.none() || e.getDistance() < enemy.getDistance() - 70 || e.getName().equals(enemy.getName())) && !isTeammate(e.getName()))
		{
			enemy.udpate(e, this);
		}
		else
		{
			for (int i = 0; i < droids.length; i++)
			{
				if ((droids[i].getName).equals(e.getName())){
					droids[i].update(e, this);
					break;
				}
			}
		}

	}

	public void onRobotDeath(RobotDeathEvent e)
	{
		if(e.getName().equals(enemy.getName()))
		{
			enemy.reset();
		}
		else if (isTeammate(e.getName()))
			droidCount--;
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