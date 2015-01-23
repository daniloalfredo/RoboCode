package MyRobots;
import robocode.*;
import java.io.IOException;

public class Pair implements java.io.Serializable{
	private double firePower;
	private double absDeg;
	public double getFirePower()
	{
		return firePower;
	}
	public double getabsDeg()
	{
		return absDeg;
	}
	public void setFirePower(double firePower)
	{
		this.firePower = firePower;
	}
	public void setabsDeg(double absDeg)
	{
		this.absDeg = absDeg;
	}

	private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.writeObject(firePower);
        stream.writeDouble(absDeg);
    }

    private void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        firePower = stream.readDouble();
        absDeg = stream.readDouble();
    }
}