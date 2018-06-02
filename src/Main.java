import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.FixedElapsedTime;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math3.genetics.UniformCrossover;

/**
 * Application single entry class.
 * 
 * @author Todor Balabanov
 */
public class Main {

	/**
	 * Pseudo-random number generator.
	 */
	private static final Random PRNG = new Random();

	/**
	 * How many levels of recursion will be applied.
	 */
	private static final int RECURSION_DEPTH = 3;

	/**
	 * Constant value to clear finite 3D space.
	 */
	private static final long EMPTY_RGB = 0x000000;

	/**
	 * Constant value for the brightest voxel.
	 */
	private static final long MOST_INTENSIVE_RGB = 0xFFFFFF;

	/**
	 * List of colors, as RGB values, which is used for the generation of the
	 * fractals.
	 */
	private static final long COLORS[] = { EMPTY_RGB, 0x080808, 0x101010, 0x181818, 0x202020, 0x282828, 0x303030,
			0x383838, 0x404040, 0x484848, 0x505050, 0x585858, 0x606060, 0x686868, 0x707070, 0x787878, 0x808080,
			0x888888, 0x909090, 0x989898, 0xA0A0A0, 0xA8A8A8, 0xB0B0B0, 0xB8B8B8, 0xC0C0C0, 0xC8C8C8, 0xD0D0D0,
			0xD8D8D8, 0xE0E0E0, 0xE8E8E8, 0xF0F0F0, 0xF8F8F8, MOST_INTENSIVE_RGB, };

	/**
	 * Finite 3D space.
	 */
	private static long target[][][] = new long[(int) Math.pow(3, RECURSION_DEPTH)][(int) Math.pow(3,
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
	 * Generate sphere in the center of the space.
	 * 
	 * @param space
	 *            Finite 3D space.
	 */
	private static void sphere(long[][][] space) {
		clear(space);

		int cx = space.length / 2;
		int cy = space[0].length / 2;
		int cz = space[0][0].length / 2;

		int r = Math.min(cx - 1, Math.min(cy - 1, cz - 1));

		for (int x = 0; x < space.length; x++) {
			for (int y = 0; y < space[x].length; y++) {
				for (int z = 0; z < space[x][y].length; z++) {
					if ((x - cx) * (x - cx) + (y - cy) * (y - cy) + (z - cz) * (z - cz) >= r * r) {
						continue;
					}

					space[x][y][z] = MOST_INTENSIVE_RGB;
				}
			}
		}
	}

	/**
	 * Generate cube in the center of the space.
	 * 
	 * @param space
	 *            Finite 3D space.
	 */
	private static void cube(long[][][] space) {
		clear(space);

		for (int x = 1 * space.length / 4; x < 3 * space.length / 4; x++) {
			for (int y = 1 * space[x].length / 4; y < 3 * space[x].length / 4; y++) {
				for (int z = 1 * space[x][y].length / 4; z < 3 * space[x][y].length / 4; z++) {
					space[x][y][z] = MOST_INTENSIVE_RGB;
				}
			}
		}
	}

	/**
	 * Fill finite discrete space with particular color.
	 * 
	 * @param space
	 *            Finite 3D discrete space.
	 * @param color
	 *            Filling color.
	 */
	private static void fill(long[][][] space, long color) {
		for (int x = 0; x < space.length; x++) {
			for (int y = 0; y < space[x].length; y++) {
				for (int z = 0; z < space[x][y].length; z++) {
					space[x][y][z] = color;
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
		long start[][][] = new long[(int) Math.pow(3, RECURSION_DEPTH)][(int) Math.pow(3, RECURSION_DEPTH)][(int) Math
				.pow(3, RECURSION_DEPTH)];
		clear(start);

		sphere(target);

		/*
		 * Initial population.
		 */
		List<Chromosome> list = new LinkedList<Chromosome>();
		for (int i = 0; i < 37; i++) {
			Long cells[] = new Long[COLORS.length * 3 * 3 * 3];
			for (int j = 0; j < cells.length; j++) {
				cells[j] = COLORS[PRNG.nextInt(COLORS.length)];
			}
			list.add(new TransitionsChromosome(cells, RECURSION_DEPTH, COLORS, target, start));
		}
		Population initial = new ElitisticListPopulation(list, 2 * list.size(), 0.1);

		// TODO Try different genetic algorithm parameters.
		GeneticAlgorithm optimizer = new GeneticAlgorithm(new UniformCrossover<TransitionsChromosome>(0.5), 0.9,
				new TransitionsMutation(COLORS), 0.1, new TournamentSelection(2));

		/*
		 * Run optimization.
		 */
		Population optimized = optimizer.evolve(initial, new FixedElapsedTime(30 * 60 * 1));

		/*
		 * Obtain result.
		 */
		System.out.println(((TransitionsChromosome) optimized.getFittestChromosome()).getRepresentation());
	}
}
