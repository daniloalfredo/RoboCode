package MyRobots;

import robocode.Robot;
import robocode.ScannedRobotEvent;
/**
 * This tests for variability in bullet power administered by the robot DaCruzer.
 *
 * @author Yong Hong Hsu.
 */
public class EnemyBot {
  //bearing, distance, energy, heading, name, velocity. 
  //All of these will be of type double except for name which will be of type String. 
  private double bearing = 0;
  private double distance = 0;
  private double energy = 0;
  private double heading = 0;
  private String name = null;
  private double velocity = 0;
  private double x = 0;
  private double y = 0;
  //Lastly, implement a public constructor which just calls reset. Note: the constructor
  //must be the same name as the class. Also, constructors never specify a return value. 
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * 
   */
  public EnemyBot() {
      reset();
  }
  //getBearing(), getDistance(), getEnergy(), getHeading(), getName(), getVelocity(). 
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return bearing bearing.
   */
  public double getBearing() {
    return bearing;
  }
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return distance distance.
   */
  public double getDistance() {
      return distance;
  }
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return energy energy.
   */
  public double getEnergy() {
      return energy;
  }
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return heading heading
   */
  public double getHeading() {
      return heading;
  }
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return name name
   */
  public String getName() {
      return name;
  }
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return velocity velocity.
   */
  public double getVelocity() {
      return velocity;
  }

  public double getx(){
    return x;
  }

  public double gety(){
    return y;
  }
  //Implement a state-change method called update which takes a ScannedRobotEvent as a parameter.
  //Call the ScannedRobotEvent's methods (same names as the ones in step #3). 
  //to set your private variables (step #2). 
  //The update method will return void.
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   *@param e event.
   */
  public void update(ScannedRobotEvent e, Robot robot) {
    bearing = e.getBearing();
    distance = e.getDistance();
    energy = e.getEnergy();
    heading = e.getHeading();
    name = e.getName();
    velocity = e.getVelocity();

    double absBearingDeg = (robot.getHeading() + e.getBearing());
    if (absBearingDeg < 0)
      absBearingDeg += 360;

    x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
    y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
  }

  public void update(ScannedRobotEvent e)
  {
    bearing = e.getBearing();
    distance = e.getDistance();
    energy = e.getEnergy();
    heading = e.getHeading();
    name = e.getName();
    velocity = e.getVelocity();
  }

  public double getFutureX(long when)
  {
    return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
  }

  public double getFutureY (long when){
    return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
  }

  //Implement another state-change method called reset which sets.
  //the name variable to the empty string ("") and. 
  //all the variables of type double to 0.0. The reset method will also return void.
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * 
   */
  public void reset() {
    bearing = 0;
    distance = 0;
    energy = 0;
    heading = 0;
    name = "";
    velocity = 0;
  }
  //Implement a (state-reporting) accessor method called none. 
  //which will return true if name is "" or false otherwise. 
  //(Remember to use the equals() method of the String class.). 
  //Basically, this method will return true if the reset method. 
  //was just called. 
  /**
   * This tests for variability in bullet power administered by the robot DaCruzer.
   *
   * @return true when true.
   */
  public boolean none() {
    return name.equals("");
  }
}