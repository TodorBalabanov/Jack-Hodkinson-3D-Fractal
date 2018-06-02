import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.MutationPolicy;

/**
 * Fractal transitions mutation.
 * 
 * @author Todor Balabanov
 */
public class TransitionsMutation implements MutationPolicy {

	/**
	 * Pseudo-random number generator.
	 */
	private static final Random PRNG = new Random();

	/**
	 * Colors used for LHS transition command.
	 */
	private static long colors[];

	/**
	 * Implementation for mutation strategy.
	 * 
	 * @param colors
	 *            Transitions colors.
	 */
	public TransitionsMutation(long[] colors) {
		TransitionsMutation.colors = colors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.math3.genetics.MutationPolicy#mutate(org.apache.commons.
	 * math3.genetics.Chromosome)
	 */
	@Override
	public Chromosome mutate(Chromosome original) throws MathIllegalArgumentException {
		/*
		 * If the type of the chromosome is unknown return the original.
		 */
		if (original instanceof TransitionsChromosome == false) {
			return original;
		}

		/*
		 * Change single color value.
		 */
		List<Long> values = new ArrayList<Long>(((TransitionsChromosome) original).getRepresentation());
		values.set(PRNG.nextInt(values.size()), colors[PRNG.nextInt(colors.length)]);

		/*
		 * Crate new chromosome with the modified value.
		 */
		return new TransitionsChromosome(values);
	}
}
