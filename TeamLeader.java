package MyRobots;
import robocode.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Locale;
import java.awt.Color;	

public class TeamLeader extends TeamRobot{
	private int moveDirection = 1;
	private int radarDirection = 1;
	private EnemyBot enemy = new EnemyBot();
	private Pair par = new Pair();
	private EnemyBot[] droids = new EnemyBot[4];
	private int droidCount = 0;
	private int tooCloseToWall = 0;
	private int wallMargin = 60;
	
	public void run()
	{
		setAllColors(Color.BLACK);
		String[] teammates = getTeammates();
		if(teammates != null)
		{
			for (int i = 0; i < teammates.length; i++)
			{
				droids[i] = new EnemyBot();
				droids[i].setName(teammates[i]);
				out.println(teammates[i]);
			}
			droidCount = teammates.length;
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
			if(!enemy.none())
				System.out.println("Enemy: " + enemy.getName() + "\n");
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
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		par.setabsDeg(absoluteBearing(x, y, futureX, futureY));
		return par;
		// non-predictive firing can be done like this:
		//double absDeg = absoluteBearing(getX(), getY(), enemy.getX(), enemy.getY());
	}

	public void doGun()
	{
		if (enemy.none())
				return;

		String message = null;
		try{
			message = String.format(Locale.US, "%f@%f@%f@%f", enemy.getx(), enemy.gety(), enemy.getHeading(), enemy.getVelocity());
			System.out.println(message);
			broadcastMessage(message);
		}catch(IOException ev)
		{
			System.out.println("Message not sent");
		}

		// turn the gun to the predicted x,y location
		Pair par = calcTiro(enemy.getDistance(), getX(), getY());
		setTurnGunRight(normalizeBearing(par.getabsDeg() - getGunHeading()));

		// if the gun is cool and we're pointed in the right direction, shoot!
		if (enemy.getDistance() < 300 && enemy.getEnergy() < 12)
		{
			tiroFatal();
		}
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10 && enemy.getDistance() < 300) {
			setFire(par.getFirePower());
		}
	}
	public void doGun2()
	{
		if (enemy.none())
				return;
		Pair par = calcTiro(enemy.getDistance(), getX(), getY());
		setTurnGunRight(normalizeBearing(par.getabsDeg() - getGunHeading()));

		// if the gun is cool and we're pointed in the right direction, shoot!
		if (enemy.getDistance() < 300 && enemy.getEnergy() < 12)
		{
			tiroFatal();
		}
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(par.getFirePower());
		}
	}
	public void doTank()
	{
		setTurnRight(normalizeBearing(enemy.getBearing() + 90 - (15 * moveDirection)));
		/*setTurnRight(enemy.getBearing() + 90);

		//strafe by changing direction every 20 ticks
		*/
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
		/*if(enemy.none())
			setTurnRadarRight(360);
		else{
			double turn = getHeading() - getRadarHeading() + enemy.getBearing();
			turn += 30 * radarDirection;
			setTurnRadarRight(normalizeBearing(turn));
			radarDirection *= -1;
		}*/
		setTurnRadarRight(360);
	}

	public void onScannedRobot(ScannedRobotEvent e)
	{
		if((enemy.none() || e.getDistance() < enemy.getDistance() - 70 || e.getName().equals(enemy.getName())) && !isTeammate(e.getName()))
		{
			enemy.update(e, this);
		}
		else if (droidCount > 0)
		{
			for (int i = 0; i < droids.length; i++)
			{
				if ((droids[i].getName()).equals(e.getName())){
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

	public void onHitByBullet(HitByBulletEvent e)
	{
		setTurnRight(e.getBearing() + 90);
		setAhead(100);
		execute();
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
