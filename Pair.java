package MyRobots;
import robocode.*;

public class Pair implements java.io.Serializable{
	private double firePower;
	private double absDeg;
	private String droidName;
	public double getFirePower()
	{
		return firePower;
	}
	public double getabsDeg()
	{
		return absDeg;
	}
	public String getDroidName()
	{
		return droidName;
	}
	public void setFirePower(double firePower)
	{
		this.firePower = firePower;
	}
	public void setabsDeg(double absDeg)
	{
		this.absDeg = absDeg;
	}
	public void setdroidName(String name)
	{
		droidName = name;
	}
}