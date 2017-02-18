import java.util.ArrayList;
import java.util.Random;

public class Boid {

	public static ArrayList<Boid> boids = new ArrayList<Boid>();

	private ArrayList<Boid> neighbours;
	protected long selfID;
	private Vector position;
	private Vector direction;

	public Boid(Vector position, Vector direction) {
		this(position, direction, new Random().nextLong());
	}

	public Boid(Vector position, Vector direction, long id) {
		this.position = position;
		this.direction = direction;
		boids.add(this);
		selfID = id;
	}

}

class Vector {

	public static Vector forward() {
		return new Vector(0, 0, 1);
	}

	public static Vector back() {
		return new Vector(0, 0, -1);
	}

	public static Vector up() {
		return new Vector(0, 1, 0);
	}

	public static Vector down() {
		return new Vector(0, -1, 0);
	}

	public static Vector left() {
		return new Vector(-1, 0, 0);
	}

	public static Vector right() {
		return new Vector(1, 0, 0);
	}

	public static Vector one() {
		return new Vector(1, 1, 1);
	}

	public static Vector zero() {
		return new Vector(0, 0, 0);
	}

	public double x;
	public double y;
	public double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Calculates the length of the vector by taking the square root of all components's squares
	 * @return
	 */
	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(x, 2));
	}

}