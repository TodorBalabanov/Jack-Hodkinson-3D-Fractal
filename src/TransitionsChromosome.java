import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.genetics.AbstractListChromosome;
import org.apache.commons.math3.genetics.InvalidRepresentationException;

/**
 * Fractal transitions chromosome.
 * 
 * @author Todor Balabanov
 */
public class TransitionsChromosome extends AbstractListChromosome<Long> {

	/**
	 * Recursive depth.
	 */
	private static int depth;

	/**
	 * Colors used for LHS transition command.
	 */
	private static long colors[];

	/**
	 * Target shape.
	 */
	private static long[][][] target;

	/**
	 * Start shape for generation.
	 */
	private static long[][][] start;

	/**
	 * End shape of generation.
	 */
	private long[][][] end;

	/**
	 * Euclidean distance between two finite discrete spaces.
	 * 
	 * @param a
	 *            First finite discrete space.
	 * @param b
	 *            Second finite discrete space.
	 * 
	 * @return Euclidean distance.
	 */
	private static double distance(long[][][] a, long[][][] b) {
		double result = 0;
		for (int x = 0; x < a.length && x < b.length; x++) {
			for (int y = 0; y < a[x].length && y < b[x].length; y++) {
				for (int z = 0; z < a[x][y].length && z < b[x][y].length; z++) {
					result += (a[x][y][z] - b[x][y][z]) * (a[x][y][z] - b[x][y][z]);
				}
			}
		}

		result = Math.sqrt(result);
		return result;
	}

	/**
	 * Fill particular 3D area with particular color.
	 * 
	 * @param color
	 *            To be used as instruction for internal fill.
	 * @param offset
	 *            Offset in the 3x3x3 patter.
	 * @param sx
	 *            Start x.
	 * @param ex
	 *            End x.
	 * @param sy
	 *            Start y.
	 * @param ey
	 *            End y.
	 * @param sz
	 *            Start z.
	 * @param ez
	 *            End z.
	 */
	private void fill(long color, int offset, int sx, int ex, int sy, int ey, int sz, int ez) {
		/*
		 * Closest color in the list of colors.
		 */
		int index = 0;
		long closest = colors[0];
		double min = Math.abs(color - closest);
		for (int i = 1; i < colors.length; i++) {
			double distance = Math.abs(color - closest);
			if (distance >= min) {
				continue;
			}

			index = i;
			closest = colors[i];
			min = distance;
		}

		/*
		 * Move to the index of the cells for the found color.
		 */
		index *= colors.length;
		index += offset;

		/*
		 * Color for the filling.
		 */
		long filler = getRepresentation().get(index);

		/*
		 * Color fill.
		 */
		for (int x = sx; x <= ex; x++) {
			for (int y = sy; y <= ey; y++) {
				for (int z = sz; z <= ez; z++) {
					end[x][y][z] = filler;
				}
			}
		}
	}

	/**
	 * Substitute bigger voxel with matrix of smaller voxels.
	 * 
	 * @param level
	 *            Recursive level.
	 * @param sx
	 *            Start x.
	 * @param ex
	 *            End x.
	 * @param sy
	 *            Start y.
	 * @param ey
	 *            End y.
	 * @param sz
	 *            Start z.
	 * @param ez
	 *            End z.
	 */
	private void substitute(int level, int sx, int ex, int sy, int ey, int sz, int ez) {
		if (level <= 0) {
			return;
		}

		int dx = (ex - sx + 1) / 3;
		int dy = (ey - sy + 1) / 3;
		int dz = (ez - sz + 1) / 3;

		for (int offset = 0, x = sx; x < ex; x += dx) {
			for (int y = sy; y < ey; y += dy) {
				for (int z = sz; z < ez; z += dz, offset++) {
					fill(end[x][y][z], offset, x, (x + dx - 1), y, (y + dy - 1), z, (z + dz - 1));
					substitute(level - 1, x, (x + dx - 1), y, (y + dy - 1), z, (z + dz - 1));
				}
			}
		}
	}

	/**
	 * Crate chromosome from list of values.
	 * 
	 * @param representation
	 *            List of values.
	 * @throws InvalidRepresentationException
	 *             Thrown when the list of values are not acceptable.
	 */
	public TransitionsChromosome(List<Long> representation) throws InvalidRepresentationException {
		super(representation);
	}

	/**
	 * Crate chromosome from array of values.
	 * 
	 * @param representation
	 *            Array of values.
	 * @param depth
	 *            Recursive depth.
	 * @param colors
	 *            Transitions colors.
	 * @param target
	 *            Target shape for fractal generation.
	 * @param start
	 *            Starting shape.
	 * @throws InvalidRepresentationException
	 *             Thrown when the list of values are not acceptable.
	 */
	public TransitionsChromosome(Long[] representation, int depth, long[] colors, long[][][] target, long[][][] start)
			throws InvalidRepresentationException {
		super(representation);
		TransitionsChromosome.depth = depth;
		TransitionsChromosome.colors = colors;
		TransitionsChromosome.target = target;
		TransitionsChromosome.start = start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.math3.genetics.Fitness#fitness()
	 */
	@Override
	public double fitness() {
		/*
		 * Copy the start state.
		 */
		end = new long[start.length][][];
		for (int x = 0; x < start.length; x++) {
			end[x] = new long[start[x].length][];
			for (int y = 0; y < start[x].length; y++) {
				end[x][y] = new long[start[x][y].length];
				for (int z = 0; z < start[x][y].length; z++) {
					end[x][y][z] = start[x][y][z];
				}
			}
		}

		/**
		 * Generate fractal.
		 */
		substitute(depth, 0, start.length - 1, 0, start[0].length - 1, 0, start[0][0].length - 1);

		/*
		 * Fitness is the Euclidean between the target shape and the final shape of the
		 * generation. Bigger fitness is for better solution.
		 */
		return Double.MAX_VALUE - distance(target, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.math3.genetics.AbstractListChromosome#
	 * newFixedLengthChromosome(java.util.List)
	 */
	@Override
	public AbstractListChromosome<Long> newFixedLengthChromosome(List<Long> original) {
		List<Long> values = new ArrayList<Long>(original);
		Collections.shuffle(values);

		return new TransitionsChromosome(values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.math3.genetics.AbstractListChromosome#checkValidity(java.
	 * util.List)
	 */
	@Override
	protected void checkValidity(List<Long> representation) throws InvalidRepresentationException {
	}

	/**
	 * Obtain solution vector.
	 * 
	 * @return Values for the solution.
	 */
	public List<Long> getRepresentation() {
		return super.getRepresentation();
	}
}
