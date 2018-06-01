import java.awt.Color;

/**
 * Application single entry class.
 * 
 * @author Todor Balabanov
 */
public class Main {
	/**
	 * How many levels of recursion will be applied.
	 */
	private static final int RECURSION_DEPTH = 3;

	/**
	 * Constant value to clear finite 3D space.
	 */
	private static final long EMPTY_RGB = Color.BLACK.getRGB();

	/**
	 * Constant value for the brightest voxel.
	 */
	private static final long MOST_INTENSIVE_RGB = Color.WHITE.getRGB();

	/**
	 * Finite 3D space.
	 */
	private static long space[][][] = new long[(int) Math.pow(3, RECURSION_DEPTH)][(int) Math.pow(3,
			RECURSION_DEPTH)][(int) Math.pow(3, RECURSION_DEPTH)];

	/**
	 * Clear finite 3D space.
	 * 
	 * @param space
	 *            Finite 3D space.
	 */
	private static void clear(long[][][] space) {
		for (int x = 0; x < space.length; x++) {
			for (int y = 0; y < space[x].length; y++) {
				for (int z = 0; z < space[x][y].length; z++) {
					space[x][y][z] = EMPTY_RGB;
				}
			}
		}
	}

	/**
	 * Application single entry point method.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {
		clear(space);
	}
}
